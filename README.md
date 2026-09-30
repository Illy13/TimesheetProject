# Researcher Timesheet Manager

---

## Introduction
This software application was developed to improve the creation, compilation, and management processes of timesheets for university researchers.
Our team focused on the design and implementation of a researcher's personal dashboard, including several useful features. 
The system provides various analytical and search tools designed to enhance user interaction and experience.

---

## Downloading the Project
To download the project, clone the repository by entering the following command into your terminal: **git clone https://github.com/Illy13/EsameTimesheetProject_final.git**. To download the necessary dependencies and compile the project, run **./gradlew build**.
Finally, to execute the application, run **./gradlew bootRun** The platform will be accessible at **localhost:8080**.

---

## System Requirements 
The system must be able to:
- Create new timesheets.
- Edit existing timesheets.
- View existing timesheets.
- Delete timesheets.
- Submit timesheet approval requests to the Principal Investigator / Scientific Supervisor.
- Compare different timesheets.
- Search for timesheets by project name.
- Sort timesheets by project name, and sort project activities by hours worked.
- Export/Download project timesheets as Excel files.
  
**Notes**:
- The approval request process is simulated, as the software component for supervisor management was not within scope.
- It is assumed that the user is already authenticated and within their personal area.

---

## Usage Scenarios

**Note:**

- System constraints refer to checks performed by the software on time logging:
  - The total working hours for a single day (the sum of hours logged across all project activities on a specific date) cannot exceed 8 hours. This constraint is checked when logging hours to an existing timesheet (on the edit and addActivity pages).
  - On the creation page, the hours logged for the initial activity must be between 1 and 8 (inclusive).
  - On the edit page, the logged hours must be between 0 and 8 (inclusive).
  - Hours cannot be logged on public holidays or weekends. 

### Scenario 1: Create a New Timesheet
- **Initial Assumptions**: 
  The user wants to create a new timesheet using the system. 
- **Main Success Scenario**:
  1. The user accesses the main page, displaying the complete list of existing timesheets (which may initially be empty) and search tools.
  2. The user clicks the "Add new timesheet" link.
  3. The user is redirected to the creation page.
  4. The user completes the form to create the timesheet and clicks "Save Timesheet".
  5. The timesheet is created and displayed on the main page alongside an actions menu.
- **Exception Handling**:
   - If submitted data violates system constraints, the system notifies the user with an error message detailing why the operation failed.
- **Other Actions**:
   - Clicking "Save Timesheet" persists the new timesheet to the database.
   - The user can choose to cancel and return to the main page, discarding any unsaved input.
- **System State upon Completion**:
  - The system presents the updated timesheet list with available action items and returns to an idle state ready for new requests.
 
### Scenario 2: Edit Existing Timesheets
**Initial Assumptions**: 
 The user wants to edit an existing timesheet using the system.

- Sub-scenario 2.1: Add an Activity to a Timesheet.  
  - **Main Success Scenario**:
  1. The user navigates to the main page displaying all timesheets.
  2. The user selects a timesheet and clicks "+ Activity".
  3. The user is redirected to the activity addition page.
  4. The user fills in the form details (activity description, hours worked, date) and clicks "Save Activity".
  5. The timesheet updates.
  - **Exception Handling**:
     - Invalid data violating system constraints triggers an error notification explaining the failure.
  - **Other Actions**:
     - Clicking "Save Activity" saves the updates to the database.
     - The user may cancel and return to the main page, discarding unsaved entries.
  - **System State upon Completion**:
     - The updated timesheet list is displayed, and the system enters an idle state.

- Sub-scenario 2.2: Modify Hours for an Existing Activity
  - **Main Success Scenario**:
  1. The user navigates to the main page.
  2. The user locates the target timesheet and clicks "Edit".
  3. The user is redirected to the edit page.
  4. The user locates the specific date/activity row in the table and modifies the logged hours.
  5. The user clicks "Save Changes".
  6. The timesheet is updated.
  - **Exception Handling**:
     - Input violating system constraints yields a descriptive error message.
  - **Other Actions**:
     - Clicking "Save Changes" persists updates to the database.
     - The user can cancel without saving.
  - **System State upon Completion**:
     - The system displays the updated list and returns to an idle state.
     
### Scenario 3: View Existing Timesheets
- **Initial Assumptions**: 
  The user wants to view an existing timesheet.
- **Main Success Scenario**:
  1. The user accesses the main page displaying all timesheets.
  2. The user locates the desired timesheet and clicks "Show".
  3. The user is redirected to the complete view page for the selected project's timesheet.
- **Exception Handling**:
   - If a timesheet was corrupted or improperly saved during creation/modification, the system prevents user interaction with that invalid entry.
- **System State upon Completion**:
   - The detailed project timesheet is displayed with option controls available:
     - Return to the main page
     - Sort activities in ascending order by hours worked
     - Download the timesheet

### Scenario 4: Delete a Timesheet.
- **Initial Assumptions**: 
  The user wants to delete an existing timesheet.
- **Main Success Scenario**:
  1. The user accesses the main page.
  2. The user selects a timesheet and clicks "Delete".
  3. The system prompts the user for confirmation.
  4. The user confirms the deletion.
  5. The timesheet is permanently removed.
- **Exception Handling**:
   - If the user decides not to proceed, the confirmation dialog allows canceling the operation safely.
- **System State upon Completion**:
   - The system displays the updated timesheet list and enters an idle state.
     
### Scenario 5: Submit Timesheet Approval Request
- **Initial Assumptions**: 
 The user wants to request supervisor approval for a timesheet.
- **Main Success Scenario**:
  1. The user accesses the main page.
  2. The user selects the target timesheet and clicks "Request Approval".
  3. The user is redirected to the review page to verify the timesheet details.
  4. The user clicks "REQUEST APPROVAL".
  5. The system displays a success message confirming submission and removes the "REQUEST APPROVAL" button.
  6. The user returns to the main page by clicking "Back to list"
- **Exception Handling**:
   - The user can exit the review screen and return to the main list without sending the request.
- **Other Actions**:
   - The approval request screen gives the user the final option to confirm or cancel before submission.
- **System State upon Completion**:
   - The main page is displayed, and the system waits for further input.
       
### Scenario 6: Compare Timesheets
- **Initial Assumptions**: 
 The user wants to compare two different timesheets.
- **Main Success Scenario**:
  1. The user accesses the main page displaying timesheets and the "Compare Timesheets" form.
  2. The user selects two timesheets from the selection form.
  3. The user clicks "Compare".
  4. The user is redirected to a page presenting the side-by-side comparison results.
  5. The user returns to the main page.
- **Exception Handling**:
   - If no projects exist in the database or if the user clicks "Compare" without making selections, an appropriate error message is displayed.
- **System State upon Completion**:
   - The system returns to the main page list view in an idle state.

### Scenario 7: Search Timesheets by Project Name
- **Initial Assumptions:**: 
 The user wants to filter timesheets using a project name search.
- **Main Success Scenario**:
  1. The user opens the main page containing the timesheet list and search bar.
  2. The user enters a project name into the search bar and clicks "Search".
  3. The system filters the view to display only matching project entries.
- **Exception Handling**:
   - Searching for a non-existent name returns an empty list; submitting an empty search query resets the view to show all project entries.
- **System State upon Completion**:
   - The filtered or full timesheet list is displayed, ready for further actions.

### Scenario 8: Sorting Functionality
- **8.1 Sort Timesheets by Project Name**. 
  - **Initial Assumptions**: 
    The user wants to order the main list alphabetically by project name.
  - **Main Success Scenario**:
  1. The user views the main list page and locates the "Sort by Project" button.
  2. The user clicks "Sort by Project".
  3. The list updates to display projects in alphabetical order.
  - **Exception Handling**:
     - The system ensures action menus and row functionalities remain intact after reordering
  - **System State upon Completion**:
     - The sorted list is shown with all action items functional.
 
- **8.2 Sort Activities by Logged Hours**.
  -  **Initial Assumptions**: 
The user wants to sort activities within a specific project timesheet by duration.
  - **Main Success Scenario**:
  1. The user navigates to the detailed view page of a selected project timesheet (Main page -> "Show").
  2. The user clicks "Sort by Hours".
  3. The system orders the activity list in ascending order based on total hours logged.
   - **Exception Handling**:
     - Action links and export capabilities are preserved following the sort operation.
  - **System State upon Completion**:
    - The detailed project view presents sorted activities with available navigation and download actions. 

### Scenario 9: Export/Download Timesheet as Excel File
- **Initial Assumptions**: 
  The user wants to download a project timesheet as a spreadsheet file (.xlsx).
- **Main Success Scenario**:
  1. The user enters the detail view page for a timesheet (Main page -> "Show").
  2. The user clicks "Download".
  3. The system generates and downloads the project timesheet as an Excel file.
- **Exception Handling**:
  - If the target download path is not configured to /User/Utente/Downloads, the system aborts the download to prevent file path errors. 
- **System State upon Completion**:
  - The user remains on the detailed project timesheet page with active navigation options. 
  
---

## Test
The application underwent thorough testing to ensure functional correctness, specifically incorporating Unit Tests and Acceptance Tests using JUnit and Selenium.

Acceptance tests were designed to execute in a strict sequential order (reflected in their naming conventions). This sequence simulates real-world usage patterns by recreating end-to-end user workflows.

**Note:**
- For reliable results, acceptance tests must be executed sequentially and atomically, as individual test states depend on preceding steps.

### Coverage
The test suite achieved the following code coverage metrics:
- Unit Test Coverage:
   ![coverage_unit_test](Immagini/unit.jpg)
 
- Acceptance Test Coverage::
  ![coverage_acceptance_test](Immagini/acceptance.jpg)
