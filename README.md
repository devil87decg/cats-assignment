# CATS — Course Application Tracking System

Team 8 · SA63 · Implementation summary updated 2 October 2026

## Current status

These features were built during our guided implementation and manually tested on Dominic's local setup. The combined team project passed `./mvnw compile`. Automated tests and startup on teammates' computers still need verification. This summary does not certify that every workshop requirement is complete; outstanding workshop audit checks remain separate.

## Implemented features

### Course fees and training days

- Internal training fees are saved as **$0**, even when an applicant enters another amount such as $50. This also applies when editing.
- Training-day counting excludes Saturdays, Sundays and dates recorded in the public-holiday table.
- Half-day requests are allowed only for internal training on one working date and count as **0.5 day**.

### Application validation

- Course title, category, start/end dates and justification are required.
- Courses must start on a future date. The end date cannot precede the start date.
- Start and end dates must be working days.
- Applications cannot overlap the applicant's own APPLIED, UPDATED or APPROVED applications.
- Editing excludes the original application from overlap and allowance calculations, avoiding double counting.
- Annual training-day and applicable fee-budget limits are checked before saving.
- Annual training-day allowances are checked against staff category: **5 days for ADMINISTRATIVE** and **10 days for PROFESSIONAL**.
- Rejected, deleted and cancelled requests are excluded from allowance totals. Pending, approved and completed requests contribute to the current allowance calculation.
- Internal training does not consume the fee budget. Paid external courses and certifications require a recorded fee budget.

### Employee actions

| Action | Behaviour |
|---|---|
| Submit | Validate and save a course application with APPLIED status. |
| View | View personal applications for the current year and application details. |
| Edit | Edit APPLIED or UPDATED applications; retain the same ID and save UPDATED status. |
| Delete | Mark a pending request DELETED, retaining its database record. |
| Cancel | Mark an approved request CANCELLED. |
| Complete | After the course ends, record experience comments and mark an approved request COMPLETED. |

Ownership checks restrict employee actions to their own applications.

### Manager functions

- View pending applications from assigned subordinates.
- Approve or reject pending requests with a decision reason.
- View assigned subordinate profiles and saved course history.
- See other subordinates' approved courses that overlap the request being reviewed.
- Access is restricted to active manager accounts and their assigned subordinates.
- Manager-only links appear on the home page. Managers can also use employee application functions through their employee profile.

### Administrator functions

- Create login accounts and assign roles.
- Create employee profiles, assign staff categories and link supervisors.
- Create or update annual training allowances.

These administrator functions are listed separately from the core employee/manager features. This implementation does not yet provide every optional administrator maintenance function, such as editing/deleting profiles or maintaining holidays through an admin page.

### Login and presentation

- Passwords are stored as BCrypt hashes.
- Incorrect login details display a message; inactive accounts are prevented from accessing protected functions.
- Logout ends the visitor's session.
- Forms display success or validation messages.
- Pages use shared CSS and tables for readability.

## Run the team version locally

### 1. Get the correct branch

The code and SQL export were verified on `dominic-cats-mvp`. A rename to `SA63-Team8-CATS` was requested, but its push has not yet been confirmed in this guide.

```bash
git clone --branch dominic-cats-mvp https://github.com/devil87decg/cats-assignment.git
cd cats-assignment
```

If the renamed branch is available, substitute `SA63-Team8-CATS` in the clone command. After the contribution is merged into main, teammates can use main instead.

### 2. Install prerequisites

- Java 21.
- MySQL running locally. The original demo uses MySQL 9.7.2; importing into an older version may need compatibility adjustments.
- Eclipse/Spring Tools or a terminal. The Maven wrapper is included.

### 3. Import the demo database

Stop CATS before importing. Use a fresh local demo database: the export includes table definitions and may replace existing cats tables.

From the repository root, on macOS/Linux:

```bash
mysql -u root -p < database/cats-demo.sql
```

Replace root with your own MySQL account if necessary. Enter your own MySQL password when prompted. The export supplies the cats schema and demo records; it does not create your local MySQL login account.

On Windows, use MySQL's client or a compatible import tool. The command above can be run in Command Prompt if mysql is on PATH; PowerShell handles redirection differently.

### 4. Configure your database connection

Create `cats/src/main/resources/application-local.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/cats
spring.datasource.username=YOUR_MYSQL_USERNAME
spring.datasource.password=YOUR_MYSQL_PASSWORD
```

Use an account with the needed permissions on cats. This file is ignored by Git. The shared application.properties activates the local profile and uses ddl-auto=update.

### 5. Start the application

```bash
cd cats
./mvnw spring-boot:run
```

On Windows, use `mvnw.cmd spring-boot:run`. Alternatively, import cats as an existing Maven project in Eclipse and run CatsApplication.

Open **http://localhost:8080/login**. Use the agreed demo credentials. The MySQL connection password is separate from CATS login passwords.

## How the parts connect

1. HTML forms submit values to controller URLs.
2. Controllers read the logged-in account from the session and call service methods.
3. Services apply business rules and use repositories to retrieve or save entities.
4. Spring Data JPA and Hibernate map Java entities to database tables and execute SQL through the MySQL driver.
5. Controllers add data to Model; Thymeleaf uses that data to render HTML returned to the browser.

Changing a Java field in memory does not by itself guarantee a database update. Our service methods use repository save calls to persist changes. The browser receives rendered HTML, not Java objects.

## Verification and remaining work

| Check | Status |
|---|---|
| Original local employee/manager/admin flows | Manual testing reported during implementation. |
| Combined team project Java compilation | BUILD SUCCESS; 28 source files compiled. |
| SQL export | Completion footer checked; file availability on the team branch verified. |
| Teammate database import and startup | Pending confirmation. |
| Automated test execution | Not run during the team integration. |
| Full workshop requirement audit | Outstanding clarification/verification checks remain. |

Remaining audit work includes historical-detail visibility, blank approval/rejection reason tests, exact allowance boundaries, year filtering, annual usage interpretation and any optional features chosen by the team. Refer to the separate workshop tracker before claiming full MVP completion.

For each teammate's setup, verify login, application listing, one submission, an edit retaining the same ID, a manager decision, and rejection of unauthorised manager access.

## Demo database sharing

`database/cats-demo.sql` is a snapshot. Later changes to Dominic's database do not update this file automatically. Each teammate runs an independent database; changes are not shared live between laptops.

The export was approved for public sharing as demo data. Keep personal database configuration out of Git. Temporary password-reset code in InitialData should be reviewed before further use because it may reset demo passwords on each startup.
