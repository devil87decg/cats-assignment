# CATS AWS Deployment Evidence

Team 8 · SA63 · 14 October 2026 · `us-east-1`

This page records the CATS application running on Amazon ECS with AWS
Fargate. It also records the Application Load Balancer, healthy target, RDS
database, ECR image and CloudWatch startup result. The screenshots below are
the same evidence captured in *CATS AWS Deployment Evidence.docx*.

## Application address

```text
http://cats-alb-1096256249.us-east-1.elb.amazonaws.com/login
```

The authenticated employee page is:

```text
http://cats-alb-1096256249.us-east-1.elb.amazonaws.com/applications
```

The local Docker copy is separate from AWS and is available at:

```text
http://localhost:8080/login
http://localhost:8080/applications
```

The ALB supplies one stable address for the website and application-generated
links. The Fargate task can be replaced without changing this address, provided
the ALB is retained.

AWS Academy resources are temporary. This URL is kept as a record of the
verified deployment. The lab may now be torn down, in which case the address
will no longer load.

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

## Deployment details

| Item | Value |
|---|---|
| AWS account | `590272943278` |
| AWS region | `us-east-1` |
| ECS cluster / service | `cats` / `cats` |
| Task definition | `cats:8` |
| Desired/running tasks | `1/1` |
| ECR repository and tag | `sa63/cats:latest` |
| Load balancer | `cats-alb` |
| Target group / state | `cats-tg` / `healthy` |
| RDS identifier | `database-1` |
| Database | MySQL 8.0.45, `db.t3.micro`, 20 GB, `cats` |
| CloudWatch log group | `/ecs/cats` |

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

## Evidence screenshots

Local Docker screenshots were captured from `http://localhost:8080`. AWS
screenshots are taken from *CATS AWS Deployment Evidence.docx*, in the same
order. No AWS access keys, database passwords, session tokens or Gmail
application passwords are included.

### Local Docker containers

![docker compose ps showing cats-cats-1 and healthy MySQL](deployment-evidence/screenshots/L0_docker_compose_ps.png)

**Figure L0.** Local Docker Compose runs the CATS container on port 8080 and a
healthy MySQL 8.4 container.

### Local CATS login

![CATS login page on localhost:8080](deployment-evidence/screenshots/L1_localhost_login.png)

**Figure L1.** The CATS login page served from Docker at
`http://localhost:8080/login`.

### Local authenticated applications

![My Applications page on localhost:8080](deployment-evidence/screenshots/L2_localhost_applications.png)

**Figure L2.** Employee My Applications loaded from the local Docker database
at `http://localhost:8080/applications`.

### Live CATS login through the ALB

![CATS login page with the ALB hostname](deployment-evidence/screenshots/01_CATS_Login_ALB_URL.png)

**Figure 1.** The CATS login page is available through the public ALB hostname.

### Authenticated application result

![Authenticated My Applications page loaded through the ALB](deployment-evidence/screenshots/02_CATS_Applications_Working.png)

**Figure 2.** The employee My Applications page loaded through ALB, ECS and RDS.

### ECR repository result

![ECR private repository sa63/cats in the AWS console](deployment-evidence/screenshots/03_ECR_Private_Repository.png)

**Figure 3.** Amazon ECR shows private repository `sa63/cats`.

### Application Load Balancer result

![Internet-facing ALB cats-alb shown as Active in the AWS console](deployment-evidence/screenshots/04_ALB_Console_Active.png)

**Figure 4.** The internet-facing Application Load Balancer is active and has a
public DNS name.

### ALB target health result

![AWS CLI target health table showing 172.31.34.94 port 8080 healthy](deployment-evidence/screenshots/05_ALB_Target_Health_CLI.png)

**Figure 5.** Target group `cats-tg` reports one healthy ECS target on port
8080.

### RDS database result

![RDS database-1 shown as Available in the AWS console](deployment-evidence/screenshots/06_RDS_Console_Available.png)

**Figure 6.** RDS MySQL database `database-1` is available.

### Deployment architecture

![Architecture overview for ALB, ECS, RDS, ECR and Gmail SMTP](deployment-evidence/screenshots/07_Architecture_ALB_ECS_RDS.png)

**Figure 7.** Browser traffic passes through ALB to ECS, which connects to
private RDS and Gmail SMTP.
