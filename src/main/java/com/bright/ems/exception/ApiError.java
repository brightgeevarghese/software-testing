package com.bright.ems.exception;

import java.time.Instant;

public record ApiError(
        String message,
        String path,
        int statusCode,
        Instant timeStamp
) {
}
