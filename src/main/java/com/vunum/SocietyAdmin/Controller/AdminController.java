package com.vunum.SocietyAdmin.Controller;

import com.vunum.SocietyAdmin.DTO.RequestDTO;
import com.vunum.SocietyAdmin.Service.ChargeCalculationService;
import com.vunum.SocietyAdmin.Service.Services;
import com.vunum.SocietyAdmin.Utilities.FileStackService;
import com.vunum.SocietyAdmin.entity.Building;
import com.vunum.SocietyAdmin.entity.Notice;
import com.vunum.SocietyAdmin.repository.BuildingManagementRepository;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;

@Slf4j
@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private Services adminService;
    @Autowired
    private ChargeCalculationService chargeCalculationService;
    @Autowired
    private BuildingManagementRepository buildingManagementRepository;
    @Autowired
    private FileStackService fileStackService;

    @PostMapping("/registerSyndic")
    public ResponseEntity<?> registerSyndic(@ModelAttribute RequestDTO.SyndicRequest request) {
        try {
            return adminService.registerSyndic(request);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getLocalizedMessage());
        }
    }

    @DeleteMapping("/syndic/{Id}")
    public ResponseEntity<?> deleteSyndic(@PathVariable("Id") Long Id) {
        try {
            return adminService.deleteSyndic(Id);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getLocalizedMessage());
        }
    }

    @PostMapping("/updateList/{Id}")
    public ResponseEntity<?> addBuildings(@PathVariable("Id") Long Id,
                                          @ModelAttribute RequestDTO.buildingRequest request) {
        return adminService.addBuildings(Id, request);
    }

    @PostMapping("/removeList/{Id}")
    public ResponseEntity<?> removeBuildingsFromSyndic(@PathVariable("Id") Long Id,
                                                       @ModelAttribute RequestDTO.buildingRequest request) {
        return adminService.removeBuildings(Id, request);
    }

    @PostMapping("/registerAdmin")
    public ResponseEntity<?> registerAdmin(@ModelAttribute RequestDTO.SyndicRequest request) {
        try {
            return adminService.registerAdmin(request);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getLocalizedMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> postLogin(@ModelAttribute RequestDTO.Commonrequest request,
                                       HttpServletRequest requests) {
        return adminService.loginPost(request, requests);
    }

    @GetMapping("/login")
    public ResponseEntity<?> getLogin(@RequestHeader("Authorization") String Auth,
                                      HttpServletRequest requests) {
        return adminService.loginGet(Auth, requests);
    }

    @PostMapping("/createUser")
    public ResponseEntity<?> createUser(@ModelAttribute RequestDTO.userRequest request) {
        return adminService.createUser(request);
    }

    @PostMapping("/createBuilding")
    public ResponseEntity<?> createBuilding(@ModelAttribute RequestDTO.buildingRequest request) {
        return adminService.createBuilding(request);
    }


    @PostMapping("/uploadNotice")
    public ResponseEntity<?> uploadFile(@ModelAttribute RequestDTO.Commonrequest request,
                                        @RequestHeader("Authorization") String auth) {
        try {
            String url = "";
            if (request.getFile() != null) url = fileStackService.uploadFile(request.getFile());

            Notice notice = adminService.uploadNoticeAdmin(request.getId(), url, request.getBuildingId(),
                    request.getTitle(), request.getData(),
                    auth.substring(7));
            return ResponseEntity.ok(notice);
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("File upload failed: " + e.getLocalizedMessage());
        }
    }

    @PostMapping("/charges/addConfig")
    public ResponseEntity<?> updateConfig(@ModelAttribute RequestDTO.BillRequest request) {
        try {
            Building building = buildingManagementRepository.findById(request.getBuildingId()).orElseThrow(() ->
                    new RuntimeException("Building not found"));
            return ResponseEntity.ok().body(chargeCalculationService.addConfig(request, building));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getLocalizedMessage());
        }
    }

    @PostMapping("/charges/insertConsumption")
    public ResponseEntity<?> insertConsumption(@ModelAttribute RequestDTO.BillRequest request) {
        try {
            return ResponseEntity.ok().body(chargeCalculationService.insertConsumption(request));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getLocalizedMessage());
        }
    }

    @PostMapping("/charges/generate")
    public ResponseEntity<?> generateBills(@ModelAttribute RequestDTO.Commonrequest requestDTO,
                                           @RequestParam("month") String billingMonth,
                                           @RequestParam("dueDate") LocalDate dueDate) {
        try {
            chargeCalculationService.calculateAndGenerateBills(requestDTO.getConsumptions(),
                    dueDate,
                    billingMonth
            );
            return ResponseEntity.ok().body("Bills generated for " + billingMonth);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getLocalizedMessage());
        }
    }

    @PostMapping("/addDirectory")
    public ResponseEntity<?> addDirectory(@ModelAttribute RequestDTO.VendorRequest request) {
        return adminService.addVendor(request);
    }

    @PutMapping("/updateDirectory/{Id}")
    public ResponseEntity<?> updateDirectory(@ModelAttribute RequestDTO.VendorRequest request,
                                             @PathVariable("Id") Long VendorId) {
        log.info("request Data {}", request.toString());
        return adminService.updateVendor(VendorId, request);
    }

    @PostMapping("/update-ticket")
    public ResponseEntity<?> updateTicket(@ModelAttribute RequestDTO.TicketRequest updatedTicket,
                                          @RequestHeader("Authorization") String auth) {
        try {
            return ResponseEntity.ok(adminService.updateTicketStatus(updatedTicket, auth.substring(7)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getLocalizedMessage());
        }
    }


    @GetMapping("/approveUser")
    public ResponseEntity<?> approveUser(@RequestParam("userId") Long Id) throws MessagingException, UnsupportedEncodingException {
        return adminService.approveUser(Id);
    }

    @GetMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String Auth,
                                    HttpServletRequest requests) {
        return adminService.logout(Auth, requests);
    }

    @GetMapping("/listSyndics")
    public ResponseEntity<?> getAllSyndics() {
        return adminService.getAllSyndics();
    }

    @GetMapping("/buildings")
    public ResponseEntity<?> getAllBuildings() {
        return adminService.getAllBuildingManagements();
    }

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        return adminService.getAllUsers();
    }

    @GetMapping("/assets")
    public ResponseEntity<?> getAllAssets() {
        return adminService.getAllAssets();
    }

    @GetMapping("/attendance")
    public ResponseEntity<?> getAllAttendance() {
        return adminService.getAllAttendance();
    }

    @GetMapping("/bills")
    public ResponseEntity<?> getAllBills() {
        return adminService.getAllBills();
    }

    @GetMapping("/categories")
    public ResponseEntity<?> getAllCategories() {
        return adminService.getAllCategories();
    }

    @GetMapping("/chargeConfigs")
    public ResponseEntity<?> getAllChargeConfigs() {
        return adminService.getAllChargeConfigs();
    }

    @GetMapping("/consumptions")
    public ResponseEntity<?> getAllConsumptions() {
        return adminService.getAllConsumptions();
    }

    @GetMapping("/dailyActivityLogs")
    public ResponseEntity<?> getAllDailyActivityLogs() {
        return adminService.getAllDailyActivityLogs();
    }

    @GetMapping("/employees")
    public ResponseEntity<?> getAllEmployees() {
        return adminService.getAllEmployees();
    }

    @GetMapping("/generalBodyMeetings")
    public ResponseEntity<?> getAllGeneralBodyMeetings() {
        return adminService.getAllGeneralBodyMeetings();
    }

    @GetMapping("/issues")
    public ResponseEntity<?> getAllHelpdesks() {
        return adminService.getAllHelpdesks();
    }

    @GetMapping("/managementEntries")
    public ResponseEntity<?> getAllManagementEntries() {
        return adminService.getAllManagementEntries();
    }

    @GetMapping("/notices")
    public ResponseEntity<?> getAllNotices() {
        return adminService.getAllNotices();
    }

    @GetMapping("/penaltyConfigs")
    public ResponseEntity<?> getAllPenaltyConfigs() {
        return adminService.getAllPenaltyConfigs();
    }

    @GetMapping("/polls")
    public ResponseEntity<?> getAllPolls() {
        return adminService.getAllPolls();
    }

    @GetMapping("/residentActivities")
    public ResponseEntity<?> getAllResidentActivities() {
        return adminService.getAllResidentActivities();
    }

    @GetMapping("/serviceRequests")
    public ResponseEntity<?> getAllServiceRequests() {
        return adminService.getAllServiceRequests();
    }

    @GetMapping("/societyComponents")
    public ResponseEntity<?> getAllSocietyComponents() {
        return adminService.getAllSocietyComponents();
    }

    @GetMapping("/subCategories")
    public ResponseEntity<?> getAllSubCategories() {
        return adminService.getAllSubCategories();
    }

    @GetMapping("/vehicles")
    public ResponseEntity<?> getAllVehicles() {
        return adminService.getAllVehicles();
    }

    @GetMapping("/vendors")
    public ResponseEntity<?> getAllVendors() {
        return adminService.getAllVendors();
    }

    @GetMapping("/votes")
    public ResponseEntity<?> getAllVotes() {
        return adminService.getAllVotes();
    }

    @GetMapping("/forum")
    public ResponseEntity<?> getAllForums() {
        return adminService.getAllForums();
    }

    @DeleteMapping("/deleteUser")
    public ResponseEntity<?> deleteUser(@RequestParam("Id") Long Id) {
        return adminService.deleteUser(Id);
    }

    @DeleteMapping("/deleteAsset")
    public ResponseEntity<?> deleteAsset(@RequestParam("Id") Long Id) {
        return adminService.deleteAsset(Id);
    }

    @DeleteMapping("/deleteAttendance")
    public ResponseEntity<?> deleteAttendance(@RequestParam("Id") Long Id) {
        return adminService.deleteAttendance(Id);
    }

    @DeleteMapping("/deleteBill")
    public ResponseEntity<?> deleteBill(@RequestParam("Id") Long Id) {
        return adminService.deleteBill(Id);
    }

    @DeleteMapping("/deleteCategory")
    public ResponseEntity<?> deleteCategory(@RequestParam("Id") Long Id) {
        return adminService.deleteCategory(Id);
    }

    @DeleteMapping("/deleteChargeConfig")
    public ResponseEntity<?> deleteChargeConfig(@RequestParam("Id") Long Id) {
        return adminService.deleteChargeConfig(Id);
    }

    @DeleteMapping("/deleteConsumption")
    public ResponseEntity<?> deleteConsumption(@RequestParam("Id") Long Id) {
        return adminService.deleteConsumption(Id);
    }

    @DeleteMapping("/deleteDailyActivityLog")
    public ResponseEntity<?> deleteDailyActivityLog(@RequestParam("Id") Long Id) {
        return adminService.deleteDailyActivityLog(Id);
    }

    @DeleteMapping("/deleteGeneralBodyMeeting")
    public ResponseEntity<?> deleteGeneralBodyMeeting(@RequestParam("Id") Long Id) {
        return adminService.deleteGeneralBodyMeeting(Id);
    }

    @DeleteMapping("/deleteHelpdesk")
    public ResponseEntity<?> deleteHelpdesk(@RequestParam("Id") Long Id) {
        return adminService.deleteHelpdesk(Id);
    }

    @DeleteMapping("/deleteManagementEntry")
    public ResponseEntity<?> deleteManagementEntry(@RequestParam("Id") Long Id) {
        return adminService.deleteManagementEntry(Id);
    }

    @DeleteMapping("/deleteNotice")
    public ResponseEntity<?> deleteNotice(@RequestParam("Id") Long Id) {
        return adminService.deleteNotice(Id);
    }

    @DeleteMapping("/deletePenaltyConfig")
    public ResponseEntity<?> deletePenaltyConfig(@RequestParam("Id") Long Id) {
        return adminService.deletePenaltyConfig(Id);
    }

    @DeleteMapping("/deletePoll")
    public ResponseEntity<?> deletePoll(@RequestParam("Id") Long Id) {
        return adminService.deletePoll(Id);
    }

    @DeleteMapping("/deleteResidentActivity")
    public ResponseEntity<?> deleteResidentActivity(@RequestParam("Id") Long Id) {
        return adminService.deleteResidentActivity(Id);
    }

    @DeleteMapping("/deleteServiceRequest")
    public ResponseEntity<?> deleteServiceRequest(@RequestParam("Id") Long Id) {
        return adminService.deleteServiceRequest(Id);
    }

    @DeleteMapping("/deleteSocietyComponent")
    public ResponseEntity<?> deleteSocietyComponent(@RequestParam("Id") Long Id) {
        return adminService.deleteSocietyComponent(Id);
    }

    @DeleteMapping("/deleteSubCategory")
    public ResponseEntity<?> deleteSubCategory(@RequestParam("Id") Long Id) {
        return adminService.deleteSubCategory(Id);
    }

    @DeleteMapping("/deleteVehicle")
    public ResponseEntity<?> deleteVehicle(@RequestParam("Id") Long Id) {
        return adminService.deleteVehicle(Id);
    }

    @DeleteMapping("/deleteVendor")
    public ResponseEntity<?> deleteVendor(@RequestParam("Id") Long Id) {
        return adminService.deleteVendor(Id);
    }

    @DeleteMapping("/deleteVote")
    public ResponseEntity<?> deleteVote(@RequestParam("Id") Long Id) {
        return adminService.deleteVote(Id);
    }


}
