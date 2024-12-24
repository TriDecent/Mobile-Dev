import {onCall} from "firebase-functions/v2/https";
import * as admin from "firebase-admin";

admin.initializeApp();

export const deleteUser = onCall(async (request) => {
  try {
    const callerUid = request.auth?.uid;
    if (!callerUid) {
      throw new Error("Unauthorized");
    }

    console.log("Caller UID:", callerUid);

    const employeesRef = admin.database().ref("employees");
    const snapshot = await employeesRef.once("value");
    const employees = snapshot.val();

    let callerEmployee = null;
    for (const key in employees) {
      if (employees[key].ID === callerUid) {
        callerEmployee = employees[key];
        break;
      }
    }

    if (!callerEmployee) {
      throw new Error(`No employee found with ID: ${callerUid}`);
    }

    console.log("Caller employee data:", callerEmployee);

    if (!callerEmployee.IsAdmin) {
      throw new Error("Only admins can delete users");
    }

    const userIdToDelete = request.data.userId;
    if (!userIdToDelete) {
      throw new Error("No user ID provided");
    }

    await admin.auth().deleteUser(userIdToDelete);
    return {isSuccess: true, error: null};
  } catch (error) {
    console.error("Error deleting user:", error);
    return {
      isSuccess: false,
      error: error instanceof Error ? error.message : "Unknown error",
    };
  }
});
