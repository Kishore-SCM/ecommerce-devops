output "eks_cluster_endpoint" { value = module.eks.cluster_endpoint }
output "eks_cluster_name"     { value = module.eks.cluster_name }
output "rds_endpoint" {
  value     = module.rds.db_instance_endpoint
  sensitive = true
}
output "redis_endpoint" {
  value = aws_elasticache_replication_group.redis.primary_endpoint_address
}
output "ecr_registry" {
  value = "${data.aws_caller_identity.current.account_id}.dkr.ecr.${var.aws_region}.amazonaws.com"
}
output "s3_bucket" { value = aws_s3_bucket.assets.bucket }
