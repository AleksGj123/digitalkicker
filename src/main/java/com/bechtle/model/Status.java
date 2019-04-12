package com.bechtle.model;

public enum Status  {
        STARTED, FINISHED, PREMATCH;

        @Override
        public String toString() {
                return super.toString().toLowerCase();
        }
}
