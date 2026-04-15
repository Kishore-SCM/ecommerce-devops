variable "aws_region" {
  description = "AWS region"
  type        = string
  default     = "ap-south-1"
}

variable "environment" {
  description = "Environment (production/staging)"
  type        = string
  default     = "production"
}

variable "domain_name" {
  description = "Primary application domain"
  type        = string
}
