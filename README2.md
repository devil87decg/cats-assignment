# CATS AWS Deployment Evidence

This folder records the CATS application running on Amazon ECS with AWS
Fargate. It also records the Application Load Balancer, healthy target, RDS
database, ECR image and CloudWatch startup result.

Evidence was captured on 14 October 2026 in `us-east-1`.

## Application address

```text
http://cats-alb-1096256249.us-east-1.elb.amazonaws.com/login
```

The authenticated employee page is:

```text
http://cats-alb-1096256249.us-east-1.elb.amazonaws.com/applications
```

The ALB supplies one stable address for the website and application-generated
links. The Fargate task can be replaced without changing this address, provided
the ALB is retained.

## How CATS is hosted

1. Maven packages the Spring Boot application as
   `cats-0.0.1-SNAPSHOT.jar`.
2. Docker packages the JAR with a Java 21 runtime.
3. The image is tagged `sa63/cats:latest` and pushed to Amazon ECR.
4. ECS task definition `cats:8` runs the image using AWS Fargate.
5. ECS service `cats` maintains one running task.
6. The internet-facing ALB `cats-alb` receives HTTP traffic on port 80.
7. Target group `cats-tg` forwards the traffic to the ECS task on port 8080.
8. The Spring Boot application connects to the private RDS MySQL database on
   port 3306.
9. Application logs are written to CloudWatch log group `/ecs/cats`.
10. The AWS profile is selected at runtime and the ALB address is supplied as
    the application base URL.

## Network controls

- The ALB security group accepts HTTP port 80 from the internet.
- The ECS security group accepts port 8080 only from the ALB security group.
- The RDS security group accepts port 3306 only from the ECS security group.
- RDS is not publicly accessible.
- ECS, ALB and RDS use VPC `vpc-0100957d7283c8837`.
- The ALB and RDS subnet group use subnets in two Availability Zones.

## Screenshots

| File | Evidence |
|---|---|
| [01_CATS_Login_ALB_URL.png](deployment-evidence/screenshots/01_CATS_Login_ALB_URL.png) | CATS login page with the ALB hostname. |
| [02_CATS_Applications_Working.png](deployment-evidence/screenshots/02_CATS_Applications_Working.png) | Authenticated My Applications page loaded through the ALB. |
| [03_ECS_Deployment_Success.png](deployment-evidence/screenshots/03_ECS_Deployment_Success.png) | ECS is active, rollout completed, and one task is running. |
| [04_ALB_Active_DNS_Address.png](deployment-evidence/screenshots/04_ALB_Active_DNS_Address.png) | ALB is active and shows its public DNS hostname. |
| [05_ALB_Target_Healthy.png](deployment-evidence/screenshots/05_ALB_Target_Healthy.png) | Target group has one healthy target on port 8080. |
| [06_RDS_Database_Available.png](deployment-evidence/screenshots/06_RDS_Database_Available.png) | Private RDS MySQL database is available. |
| [07_ECR_Image.png](deployment-evidence/screenshots/07_ECR_Image.png) | The latest CATS image is stored in ECR. |
| [08_CloudWatch_Startup_No_Errors.png](deployment-evidence/screenshots/08_CloudWatch_Startup_No_Errors.png) | Application started with the AWS profile and no recent errors were found. |
| [09_Architecture_ALB_ECS_RDS.png](deployment-evidence/screenshots/09_Architecture_ALB_ECS_RDS.png) | Architecture overview for ALB, ECS, RDS, ECR and Gmail SMTP. |

The AWS infrastructure screenshots show the results returned by AWS CLI. No
AWS access keys, database passwords or email credentials are included.

## Deployment details

- AWS account: `590272943278`
- AWS region: `us-east-1`
- ECS cluster: `cats`
- ECS service: `cats`
- Task definition: `cats:8`
- Desired/running tasks: `1/1`
- ECR repository and tag: `sa63/cats:latest`
- Load balancer: `cats-alb`
- Target group: `cats-tg`
- Target state: `healthy`
- RDS identifier: `database-1`
- Database engine: MySQL 8.0.45
- Database class: `db.t3.micro`
- Database storage: 20 GiB
- Database name: `cats`
- CloudWatch log group: `/ecs/cats`

## Verified result

- The ALB login page returned HTTP 200.
- Administrator authentication opened the CATS administration page.
- Employee authentication opened the My Applications page.
- ECS reported one running task and a completed rollout.
- The ALB target was healthy.
- RDS reported `available`.
- CloudWatch recorded `Started CatsApplication`.
- No recent application errors were found during verification.

Successful authentication and application data display confirm the full path:

```text
Browser -> ALB -> ECS Fargate -> RDS MySQL
```

## Scope

