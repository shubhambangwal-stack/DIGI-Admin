package com.vunum.SocietyAdmin.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
public class RequestDTO {
    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class Commonrequest {
        private Long Id;
        private String userName;
        private String password;
        private List<MultipartFile> photos;
        private List<MultipartFile> files;
        private List<Long> consumptions;
        private MultipartFile file;
        private MultipartFile photo;
        private String data;
        private String title;
        private Long BuildingId;
        private String email;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class userRequest {
        private Long Id;
        private String firstName;
        private String lastName;
        private String password;
        private String email;
        private String phoneNumber;
        private Integer floo;
        private Integer flatNumber;
        private String boxNumber;
        private String flatType;
        private Integer floor;
        private Long buildingId;
        private String role;

    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class buildingRequest {
        private Long Id;
        private Long syndicId;
        private String buildingName;
        private String streetName;
        private Long buildingNumber;
        private List<Long> buildingIds;
        private Long pincode;
        private String city;
        private String syndicName;
        private String syndicAddress;
        private String syndicBTW;
        private String syndicAdminEmail;
        private String syndicWebsite;
        private MultipartFile syndicLogo;
        private String adminName;

    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class VendorRequest {
        private Long id;
        private String name;
        private String contactPerson;
        private String phoneNumber;
        private String email;
        private String serviceType;
        private String availability;
        private Long buildingId;
        private String type;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class SyndicRequest {
        private String email;
        private String password;

        private String syndicName;
        private String syndicAddress;
        private String syndicBTW;
        private String syndicAdminEmail;
        private String syndicWebsite;
        private MultipartFile syndicLogo;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class BillRequest {
        private Long userId;
        private Long buildingId;
        private String configType;
        private String flatType;
        private String phoneNumber;
        private Double unitsConsumed;
        private Double gasUnitsConsumed;
        private String type;
        private String BillingMonth;
        private String residentType;
        private Double reserveCharge;
        private Double baseRate;
        private Double extraChargePercentage;
        private Double maintenanceRate;
        private String Source;
        private Map<String, Double> customUtils;

    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class TicketRequest {
        private Long Id;
        private String ticketStatus;
        private String ticketResponse;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class MeetingRequest {
        private String title;
        private String agenda;
        private MultipartFile file;
        private String location;
        private String Description;
        private LocalDateTime scheduledTime;
        private List<Long> attendees;
        private String type;

    }

    @Data
    public static class PollRequest {
        private Long meetingId;
        private String question;
        private List<String> options;
        private LocalDate startDate;
        private LocalDate endDate;
    }

    @Data
    public static class EmployeeDTO {
        public String role;
        public String email;
        public String name;
        public String password;
        public String phoneNumber;
        private String token;
        private Long buildingId;
    }

    @Data
    public static class ShiftRequest {
        public String name;
        private String shiftStart;
        private String shiftEnd;
        private String shiftType;

    }

}
