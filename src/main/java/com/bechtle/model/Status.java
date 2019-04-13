package com.bechtle.model;

public enum Status  {
        STARTED, FINISHED, PREMATCH, CANCEL_REQUEST;

        @Override
        public String toString() {
                return super.toString().toLowerCase();
        }
}
