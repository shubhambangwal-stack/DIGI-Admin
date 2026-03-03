package com.vunum.SocietyAdmin.Controller;

import com.vunum.SocietyAdmin.DTO.RequestDTO;
import com.vunum.SocietyAdmin.Service.ChargeCalculationService;
import com.vunum.SocietyAdmin.Service.Services;
import com.vunum.SocietyAdmin.Utilities.FileStackService;
import com.vunum.SocietyAdmin.entity.Building;
import com.vunum.SocietyAdmin.entity.Employee;
import com.vunum.SocietyAdmin.entity.Notice;
import com.vunum.SocietyAdmin.repository.BuildingManagementRepository;
import com.vunum.SocietyAdmin.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;

@Slf4j
@RestController
@RequestMapping("/syndic")
public class SyndicController {
    @Autowired
    private Services adminService;
    @Autowired
    private BuildingManagementRepository buildingManagementRepository;
    @Autowired
    private ChargeCalculationService chargeCalculationService;
    @Autowired
    private FileStackService fileStackService;
    @Autowired
    private UserRepository userRepository;

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

    @GetMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String Auth,
                                    HttpServletRequest requests) {
        try {
            return adminService.logout(Auth, requests);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getLocalizedMessage());
        }
    }

    @PostMapping("/addConfig")
    public ResponseEntity<?> updateConfig(@ModelAttribute RequestDTO.BillRequest request) {
        try {
            Building building = buildingManagementRepository.findById(request.getBuildingId()).orElseThrow(() ->
                    new RuntimeException("Building not found"));
            return ResponseEntity.ok(chargeCalculationService.addConfig(request, building));
        } catch (Exception e) {
            log.info("Exception {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getLocalizedMessage());
        }
    }

    @GetMapping("/buildings")
    public ResponseEntity<?> getAllBuildings(@RequestHeader("Authorization") String auth) {
        return adminService.getAllBuilidngsSyndic(auth.substring(7));
    }

    @GetMapping("/bills")
    public ResponseEntity<?> getAllBills(@RequestHeader("Authorization") String auth) {
        return adminService.getAllBillsSyndic(auth.substring(7));
    }

    @GetMapping("/getAll")
    public ResponseEntity<?> getAll(@RequestHeader("Authorization") String auth) {
        return adminService.getAllUsersSyndic(auth.substring(7));
    }

    @GetMapping("/getTenants")
    public ResponseEntity<?> getTenants(@RequestHeader("Authorization") String auth) {
        return adminService.getTenants(auth.substring(7));
    }

    @GetMapping("/getOwners")
    public ResponseEntity<?> getOwners(@RequestHeader("Authorization") String auth) {
        return adminService.getOwners(auth.substring(7));
    }

    @GetMapping("/getEmployees")
    public ResponseEntity<?> getEmployees(@RequestHeader("Authorization") String auth) {
        return adminService.getEmployees(auth.substring(7));
    }

    @GetMapping("/getConfig/{buildingId}")
    public ResponseEntity<?> getConfig(@PathVariable("buildingId") Long buildingId) {
        try {
            Building building = buildingManagementRepository.findById(buildingId).orElseThrow(() ->
                    new RuntimeException("Building not found"));
            return ResponseEntity.ok(chargeCalculationService.getConfig(building));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getLocalizedMessage());
        }
    }

    @GetMapping("/getBillConfigs")
    public ResponseEntity<?> getAllConfig(@RequestHeader("Authorization") String token) {
        try {
            return ResponseEntity.ok(adminService.getAllConfigs(token.substring(7)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getLocalizedMessage());
        }
    }

    @PostMapping("/insertConsumption")
    public ResponseEntity<?> insertConsumption(@ModelAttribute RequestDTO.BillRequest request) {
        try {
            return ResponseEntity.ok().body(chargeCalculationService.insertConsumption(request));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getLocalizedMessage());
        }
    }

    @GetMapping("/consumptions")
    public ResponseEntity<?> getAllConsumptions(@RequestHeader("Authorization") String token) {
        return adminService.getAllConsumptionsSyndic(token);
    }

    @GetMapping("/consumptions/unbilled")
    public ResponseEntity<?> getUnbilledConsumptions(@RequestHeader("Authorization") String token) {
        return adminService.getUnbilledConsumptionsSyndic(token.substring(7));
    }

    @PostMapping("/generate")
    public ResponseEntity<?> generateBills(@ModelAttribute RequestDTO.Commonrequest requestDTO,
                                           @RequestParam("month") String billingMonth,
                                           @RequestParam("dueDate") LocalDate dueDate) throws MessagingException, IOException {
//        try {
        chargeCalculationService.calculateAndGenerateBills(requestDTO.getConsumptions(),
                dueDate,
                billingMonth
        );
        return ResponseEntity.ok().body("Bills generated for " + billingMonth);
//        } catch (Exception e) {
//            log.info("Error {}",e.getMessage());
//            return ResponseEntity.badRequest().body(e.getLocalizedMessage());
//        }

    }

    @PostMapping("/generateGarage")
    public ResponseEntity<?> generateGarageBills(@ModelAttribute RequestDTO.Commonrequest requestDTO,
                                                 @RequestParam("month") String billingMonth,
                                                 @RequestParam("dueDate") LocalDate dueDate,
                                                 @RequestParam("reserveCharge") Double reserve) {
        try {
            chargeCalculationService.calculateAndGenerateGarageBills(requestDTO.getConsumptions(),
                    dueDate,
                    billingMonth,
                    reserve
            );
            return ResponseEntity.ok().body("Bills generated for " + billingMonth);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getLocalizedMessage());
        }
    }

    @PostMapping("/createMeeting")
    public ResponseEntity<?> createMeeting(@ModelAttribute RequestDTO.MeetingRequest request,
                                           @RequestHeader("Authorization") String token) {
        try {
            return ResponseEntity.ok().body(adminService.createMeeting(request, token.substring(7)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getLocalizedMessage());
        }
    }

    @GetMapping("/getMeetings")
    public ResponseEntity<?> getMeeting(@RequestHeader("Authorization") String token) {
        try {
            return ResponseEntity.ok().body(adminService.getMeetings(token.substring(7)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getLocalizedMessage());
        }
    }

    @GetMapping("/getMeeting/{Id}")
    public ResponseEntity<?> getOneMeeting(@PathVariable("Id") long Id) {
        try {
            return ResponseEntity.ok().body(adminService.getMeeting(Id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getLocalizedMessage());
        }
    }

    @PostMapping("/createPoll")
    public ResponseEntity<?> createPoll(@ModelAttribute RequestDTO.PollRequest request,
                                        @RequestHeader("Authorization") String token) {
        try {
            log.info("request data {}", request);
            return ResponseEntity.ok().body(adminService.createPoll(request));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getLocalizedMessage());
        }
    }

    @GetMapping("/closePoll/{id}")
    public ResponseEntity<?> closePoll(@PathVariable("id") Long Id) {
        try {
            return ResponseEntity.ok().body(adminService.closePoll(Id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getLocalizedMessage());
        }
    }

    @GetMapping("/{pollId}/results")
    public ResponseEntity<?> getPollResults(@PathVariable("pollId") Long pollId) {
        try {
            return ResponseEntity.ok().body(adminService.getPollResults(pollId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getLocalizedMessage());
        }
    }

    @PostMapping("/addDirectory")
    public ResponseEntity<?> addDirectory(@RequestHeader("Authorization") String Auth,
                                          @ModelAttribute RequestDTO.VendorRequest request) {
        return adminService.addVendor(request);
    }

    @PutMapping("/updateDirectory/{Id}")
    public ResponseEntity<?> updateDirectory(@RequestHeader("Authorization") String Auth,
                                             @ModelAttribute RequestDTO.VendorRequest request,
                                             @PathVariable("Id") Long VendorId) {
        log.info("request Data {}", request.toString());
        return adminService.updateVendor(VendorId, request);
    }

    @GetMapping("/vendors")
    public ResponseEntity<?> getAllVendors(@RequestHeader("Authorization") String Auth) {
        return adminService.getAllVendors(Auth);
    }

    @DeleteMapping("/deleteVendor")
    public ResponseEntity<?> deleteVendor(@RequestParam("Id") Long Id,
                                          @RequestHeader("Authorization") String Auth) {
        return adminService.deleteVendor(Id);
    }


    @GetMapping("/forums")
    public ResponseEntity<?> getAllForums(@RequestHeader("Authorization") String Auth) {
        try {
            return adminService.getAllForumsSyndic(Auth.substring(7));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getLocalizedMessage());
        }
    }


    @DeleteMapping("/deleteNotice")
    public ResponseEntity<?> deleteFile(@RequestParam("id") Long id,
                                        @RequestHeader("Authorization") String Auth) {
        try {
            adminService.deleteNotice2(id, Auth.substring(7));
            return ResponseEntity.ok("File Deleted");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("File deletion failed: " + e.getLocalizedMessage());
        }
    }

    @GetMapping("/notice")
    public ResponseEntity<?> getNotice(@RequestHeader("Authorization") String auth) {
        try {

            return ResponseEntity.ok(adminService.getAllNoticesSyndic(auth.substring(7)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("File deletion failed: " + e.getLocalizedMessage());
        }
    }

    @PostMapping("/uploadNotice")
    public ResponseEntity<?> uploadFile(@ModelAttribute RequestDTO.Commonrequest request,
                                        @RequestHeader("Authorization") String auth) {
        try {
            String url = "";
            if (request.getFile() != null) url = fileStackService.uploadFile(request.getFile());
            Long id = request.getId();
            Notice notice = adminService.uploadNoticeSyndic(id, url, request.getBuildingId(),
                    request.getTitle(), request.getData(), request.getFile(),
                    auth.substring(7));
            return ResponseEntity.ok(notice);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("File upload failed: " + e.getLocalizedMessage());
        }
    }

    @GetMapping("/issues")
    public ResponseEntity<?> getAllHelpdesks() {
        return adminService.getAllHelpdesks();
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
    public ResponseEntity<?> approveUser(@ModelAttribute("userId") Long Id) throws MessagingException,
            UnsupportedEncodingException {
        return adminService.approveUser(Id);
    }

    @DeleteMapping("/deleteEmployee")
    public ResponseEntity<?> deleteEmployee(@RequestParam("Id") Long Id) {
        return adminService.deleteEmployee(Id);
    }

    @DeleteMapping("/deleteForum")
    public ResponseEntity<?> deleteForum(@RequestParam("Id") Long Id) {
        try {
            return adminService.deleteForum(Id);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getLocalizedMessage());
        }
    }

    @DeleteMapping("/deleteMeeting")
    public ResponseEntity<?> deleteMeeting(@RequestParam("Id") Long Id) {
        try {
            return adminService.deleteGeneralBodyMeeting(Id);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getLocalizedMessage());
        }
    }

    @DeleteMapping("/deleteUser")
    public ResponseEntity<?> deleteUser(@RequestParam("Id") Long Id) {
        return adminService.deleteUser(Id);
    }

    @PostMapping("/registerEmployee")
    public ResponseEntity<?> registerEmployee(@ModelAttribute RequestDTO.EmployeeDTO request,
                                              @RequestHeader("Authorization") String auth) {
        try {

            Employee employee = adminService.registerEmployee(request, auth.substring(7));
            return ResponseEntity.status(HttpStatus.CREATED).body(employee);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getLocalizedMessage());
        }
    }

    @PostMapping("/assignBuilding/{employeeId}")
    public ResponseEntity<?> assignBuilding(@PathVariable("employeeId") Long employeeId,
                                            @ModelAttribute RequestDTO.buildingRequest requestId) {
        try {
            return ResponseEntity.ok(adminService.assignBuilding(employeeId, requestId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getLocalizedMessage());
        }
    }

    @PostMapping("/deassignBuilding/{employeeId}/{buildingId}")
    public ResponseEntity<?> deAssignBuilding(@PathVariable("employeeId") Long employeeId,
                                              @PathVariable("buildingId") Long buildingId) {
        try {
            return ResponseEntity.ok(adminService.deAssignBuilding(employeeId, buildingId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getLocalizedMessage());
        }
    }

    @PostMapping("/Shifts")
    public ResponseEntity<?> createShift(@ModelAttribute RequestDTO.ShiftRequest request,
                                         @RequestHeader("Authorization") String auth) {
        try {
            return ResponseEntity.ok(adminService.createShift(request, auth.substring(7)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getLocalizedMessage());
        }
    }

    @GetMapping("/Shifts")
    public ResponseEntity<?> getShift(@RequestHeader("Authorization") String auth) {
        try {
            return ResponseEntity.ok(adminService
                    .getAllShifts(auth.substring(7)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getLocalizedMessage());
        }
    }

    @DeleteMapping("/Shifts/{Id}")
    public ResponseEntity<?> deleteShift(@PathVariable("Id") Long Id) {
        try {
            return ResponseEntity.ok(adminService
                    .deleteShift(Id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getLocalizedMessage());
        }
    }

    @PostMapping("/assignShift/{shiftId}/{Id}")
    public ResponseEntity<?> assignShift(@PathVariable("Id") Long Id,
                                         @PathVariable("shiftId") Long shiftId) {
        try {
            return ResponseEntity.ok(adminService.assignShift(Id, shiftId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getLocalizedMessage());
        }
    }

    @GetMapping("/attendance")
    public ResponseEntity<?> getAllAttendance() {
        return adminService.getAllAttendance();
    }

    @GetMapping("/attendance/{Id}")
    public ResponseEntity<?> getSpecificAttendance(@PathVariable("Id") Long Id) {
        return adminService.getSpecificAttendance(Id);
    }

    @PostMapping("/attendance/{Id}")
    public ResponseEntity<?> postAttendance(@PathVariable("Id") Long Id,
                                            @RequestParam("buildingId") Long buildingId) {
        return adminService.postAttendance(Id, buildingId);
    }
}
