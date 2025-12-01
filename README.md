# WorkManager Demo

This repository contains an Android application designed to demonstrate the usage of **WorkManager** for background processing. The project is divided into multiple branches, each illustrating different concepts and complexity levels of WorkManager implementation.

## Branches Overview

### 1. `1-SimpleWorkManager`
This branch demonstrates a basic use case of WorkManager: **Image Filtering**.

-   **Scenario**: The user picks an image from the gallery, and a background worker applies a grayscale filter to it.
-   **Key Concepts**:
    -   `OneTimeWorkRequest`: For a single execution of the task.
    -   **Input/Output Data**: Passing the image URI to the worker and receiving the filtered image URI back.
    -   **Basic Constraints**: Uses `setRequiresStorageNotLow(true)` to ensure the device has enough storage.
    -   **Observation**: Observing `WorkInfo` to update the UI (ProgressBar, Result Image) based on the work state (`SUCCEEDED`, `RUNNING`, etc.).

### 2. `2-SimpleWorkManagerWithConstraint`
This branch demonstrates a more advanced use case with strict constraints: **Simulated Asset Download**.

-   **Scenario**: The user initiates a large file download (simulated). The task is designed to run only when specific conditions are met to preserve battery and data.
-   **Key Concepts**:
    -   **Strict Constraints**:
        -   `setRequiredNetworkType(NetworkType.UNMETERED)`: Requires Wi-Fi to avoid data charges.
        -   `setRequiresCharging(true)`: Requires the device to be charging to save battery.
        -   `setRequiresStorageNotLow(true)`: Ensures sufficient storage space.
    -   **Progress Updates**: The worker reports intermediate progress (0-100%), which is observed and displayed in the UI.
    -   **Status Handling**: The UI explicitly shows states like "Enqueued (Waiting for constraints)", "Downloading...", "Blocked", etc., helping users understand why a task might not run immediately.


