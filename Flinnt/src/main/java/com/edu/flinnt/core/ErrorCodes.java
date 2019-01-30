package com.edu.flinnt.core;

import com.edu.flinnt.protocol.User;

import static com.edu.flinnt.R.string.course;

/**
 * class contains the list of error codes
 */
public class ErrorCodes {

    // General Message
    public static final int ERROR_CODE_1 = 1; // One or more parameters required to process request are missing
    public static final int ERROR_CODE_2 = 2; // Invalid value specified for parameter
    public static final int ERROR_CODE_3 = 3; // An unexpected error occurred while processing request. Internal server error.
    public static final int ERROR_CODE_4 = 4; // Invalid coupon code
    public static final int ERROR_CODE_5 = 5; // Unable to find information in database for specified primary key value
    public static final int ERROR_CODE_6 = 6; // Empty value specified which is not allowed.
    public static final int ERROR_CODE_7 = 7; // Specified values already exists in database and duplicate values is not allowed for the field.
    public static final int ERROR_CODE_8 = 8; // User is not authorized to execute the requested action.
    public static final int ERROR_CODE_9 = 9; // Requested resource does not exist. Resource could be a File.
    public static final int ERROR_CODE_15 = 15;// You are not authorised to post this comment.

    // Account module
    public static final int ERROR_CODE_300 = 300; // Invalid email address
    public static final int ERROR_CODE_301 = 301; // Invalid mobile number
    public static final int ERROR_CODE_302 = 302; // Invalid password specified. Password could not be blank and must be at least 6 characters long
    public static final int ERROR_CODE_303 = 303; // Account has been suspended
    public static final int ERROR_CODE_304 = 304; // Add correct username and password while login.
    public static final int ERROR_CODE_305 = 305; // Invalid verification code. Reason: verification code does not below to current user verification code does not exist at all
    public static final int ERROR_CODE_306 = 306; // Verification code is expired.
    public static final int ERROR_CODE_307 = 307; // User does not exists
    public static final int ERROR_CODE_308 = 308; // User account has already been verified
    public static final int ERROR_CODE_309 = 309; // User account verification is pending

    // Post module
    public static final int ERROR_CODE_400 = 400; // required fields are not passed in request or validation fails
    public static final int ERROR_CODE_401 = 401; //  If user is not authorized to perform this action
    public static final int ERROR_CODE_402 = 402; // Title is larger than 255 characters
    public static final int ERROR_CODE_557 = 557; // If any error occurred recording answer
    public static final int ERROR_CODE_403 = 403; // User account does not exists or course does not exists
    public static final int ERROR_CODE_409 = 409; // User has already voted for this poll


    // Course module
    public static final int ERROR_CODE_500 = 500; // User have entered invalid course code or related course status is not valid for operation being performed
    public static final int ERROR_CODE_501 = 501; // User has already subscribed to course

    public static final int ERROR_CODE_507 = 507; // Course has been deleted

    // Resourse module
    public static final int ERROR_CODE_600 = 600; // Uploaded file is of size zero or file is corrupted during transmission

    // Content Edit module
    public static final int ERROR_CODE_701 = 701; // Can't delete/HideShow content!/Section There is already a copy content process running on this course.

    public static final int ERROR_CODE_702 = 702; // Already finished Quiz


}

