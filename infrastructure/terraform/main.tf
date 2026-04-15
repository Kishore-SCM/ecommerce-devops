terraform {
  required_version = ">= 1.6"
  required_providers {
    aws = { source = "hashicorp/aws", version = "~> 5.0" }
  }
  backend "s3" {
    bucket         = "ecommerce-terraform-state"
    key            = "production/terraform.tfstate"
    region         = "ap-south-1"
    encrypt        = true
    dynamodb_table = "terraform-locks"
  }
}

provider "aws" { region = var.aws_region }

module "vpc" {
  source  = "terraform-aws-modules/vpc/aws"
  version = "5.2.0"
  name                 = "ecommerce-vpc"
  cidr                 = "10.0.0.0/16"
  azs                  = ["ap-south-1a", "ap-south-1b", "ap-south-1c"]
  private_subnets      = ["10.0.1.0/24","10.0.2.0/24","10.0.3.0/24"]
  public_subnets       = ["10.0.101.0/24","10.0.102.0/24","10.0.103.0/24"]
  enable_nat_gateway   = true
  single_nat_gateway   = false
  enable_dns_hostnames = true
  tags = {
    "kubernetes.io/cluster/ecommerce-cluster" = "shared"
    Environment = var.environment
  }
  public_subnet_tags  = { "kubernetes.io/role/elb"           = "1" }
  private_subnet_tags = { "kubernetes.io/role/internal-elb"  = "1" }
}

module "eks" {
  source  = "terraform-aws-modules/eks/aws"
  version = "19.21.0"
  cluster_name    = "ecommerce-cluster"
  cluster_version = "1.33"
  vpc_id          = module.vpc.vpc_id
  subnet_ids      = module.vpc.private_subnets
  cluster_endpoint_public_access = true
  eks_managed_node_groups = {
    general = {
      instance_types = ["t3.micro"]
      min_size = 2; max_size = 6; desired_size = 3
      labels = { role = "general" }
    }
  }
  tags = { Environment = var.environment, Project = "ecommerce" }
}

module "rds" {
  source  = "terraform-aws-modules/rds/aws"
  version = "6.3.0"
  identifier            = "ecommerce-db"
  engine                = "postgres"
  engine_version        = "15.4"
  instance_class        = "db.t3.medium"
  allocated_storage     = 20
  max_allocated_storage = 100
  db_name               = "ecommerce"
  username              = "postgres"
  manage_master_user_password = true
  multi_az                    = true
  deletion_protection         = true
  backup_retention_period     = 7
  vpc_security_group_ids = [aws_security_group.rds.id]
  db_subnet_group_name   = aws_db_subnet_group.main.name
  tags = { Environment = var.environment }
}

resource "aws_elasticache_replication_group" "redis" {
  replication_group_id       = "ecommerce-redis"
  description                = "Redis for ecommerce"
  node_type                  = "cache.t3.micro"
  num_cache_clusters         = 2
  automatic_failover_enabled = true
  at_rest_encryption_enabled = true
  transit_encryption_enabled = true
  subnet_group_name          = aws_elasticache_subnet_group.main.name
  security_group_ids         = [aws_security_group.redis.id]
}

locals {
  services = ["api-gateway","product-service","order-service","user-service","notification-service","frontend"]
}

resource "aws_ecr_repository" "services" {
  for_each             = toset(local.services)
  name                 = each.value
  image_tag_mutability = "MUTABLE"
  image_scanning_configuration { scan_on_push = true }
}

resource "aws_s3_bucket" "assets" {
  bucket = "ecommerce-assets-${var.environment}"
}

resource "aws_db_subnet_group" "main" {
  name       = "ecommerce-db-subnet"
  subnet_ids = module.vpc.private_subnets
}

resource "aws_elasticache_subnet_group" "main" {
  name       = "ecommerce-redis-subnet"
  subnet_ids = module.vpc.private_subnets
}

resource "aws_security_group" "rds" {
  name   = "ecommerce-rds-sg"
  vpc_id = module.vpc.vpc_id
  ingress {
    from_port       = 5432
    to_port         = 5432
    protocol        = "tcp"
    security_groups = [module.eks.node_security_group_id]
  }
}

resource "aws_security_group" "redis" {
  name   = "ecommerce-redis-sg"
  vpc_id = module.vpc.vpc_id
  ingress {
    from_port       = 6379
    to_port         = 6379
    protocol        = "tcp"
    security_groups = [module.eks.node_security_group_id]
  }
}

data "aws_caller_identity" "current" {}
