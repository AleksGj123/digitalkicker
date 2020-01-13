package com.bechtle.model;

public enum Status  {
        STARTED, FINISHED, PREMATCH, CANCEL_REQUEST, REMATCH_NO, REMATCH_YES;

        @Override
        public String toString() {
                return super.toString().toLowerCase();
        }
}
