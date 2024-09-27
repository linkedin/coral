# Coral GitHub Actions Workflows

This directory contains the GitHub Actions workflows for the Coral project. These workflows automate various processes including continuous integration, release management, and version deprecation.

## Workflows

### 1. [Continuous Integration (CI)](./ci.yml)

The CI workflow is responsible for building, testing, and releasing the Coral project.

**Trigger:**
- Push to `master` branch
- Pull requests to any branch

**Key Steps:**
1. Check out code
2. Set up Java
3. Perform build
4. Run tests
5. Perform release (only on push to `master`)

**Usage:**
This workflow runs automatically on push and pull request events. No manual intervention is required for normal operation.

### 2. [Version Deprecation](./deprecation.yml)

The Version Deprecation workflow handles the deprecation of older Coral versions, either manually or automatically based on configured criteria.

**Trigger:**
- Manual workflow dispatch

**Key Steps:**
1. Check if the user triggering the workflow has maintainer or admin permissions
2. Check out code
3. Set up Java
4. Manually deprecate specified versions
5. (TODO) Auto-deprecate old versions based on criteria

**Usage:**
To use this workflow:

1. Go to the "Actions" tab in the Coral GitHub repository
2. Select the "Version Deprecation" workflow
3. Click "Run workflow"
4. Fill in the inputs:
   - For manual deprecation: Enter versions in "Versions to deprecate" (comma-separated)
   - For auto-deprecation: Set "Run auto-deprecate" to true
   - Optionally adjust the age and version difference parameters
5. Click "Run workflow"

**Important Note:** This workflow is restricted to users with maintainer or admin permissions on the repository. If a user without these permissions attempts to run the workflow, it will fail with an error message.
