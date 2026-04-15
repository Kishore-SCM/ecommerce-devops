bucket         = "ecommerce-terraform-state"
key            = "production/terraform.tfstate"
region         = "ap-south-1"
encrypt        = true
dynamodb_table = "terraform-locks"
