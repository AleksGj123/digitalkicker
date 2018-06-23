package com.bechtle.api.service;

public class ErrorHandler {
        private String message;

        public ErrorHandler(String msg) {
            this.message = msg;
        }

        public String getMessage() {
            return this.message;
        }
}
