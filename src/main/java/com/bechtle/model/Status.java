package com.bechtle.model;

public enum Status  {
        STARTED, FINISHED;

        @Override
        public String toString() {
                return super.toString().toLowerCase();
        }
}
