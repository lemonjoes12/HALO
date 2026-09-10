package com.ptc.halo.dtoResponse;

import com.ptc.halo.enums.Status;
import com.ptc.halo.enums.YearLevel;

public class StudentResponse {

        private String name;
        private String email;
        private Status status;

        private String studentId;
        private String section;
        private YearLevel yearLevel;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public YearLevel getYearLevel() {
            return yearLevel;
        }

        public void setYearLevel(YearLevel yearLevel) {
            this.yearLevel = yearLevel;
        }

        public String getSection() {
            return section;
        }

        public void setSection(String section) {
            this.section = section;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getStudentId() {
            return studentId;
        }

        public void setStudentId(String studentId) {
            this.studentId = studentId;
        }
    }


