package com.vunum.SocietyAdmin.Service;

import com.vunum.SocietyAdmin.DTO.RequestDTO;
import com.vunum.SocietyAdmin.Utilities.*;
import com.vunum.SocietyAdmin.entity.*;
import com.vunum.SocietyAdmin.repository.*;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class Services {
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AssetRepository assetRepository;
    @Autowired
    private AttendanceRepository attendanceRepository;
    @Autowired
    private BillRepository billRepository;
    @Autowired
    private BuildingManagementRepository buildingManagementRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ChargeConfigRepository chargeConfigRepository;
    @Autowired
    private ConsumptionRepository consumptionRepository;
    @Autowired
    private DailyActivityLogRepository dailyActivityLogRepository;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private GeneralBodyMeetingRepository generalBodyMeetingRepository;
    @Autowired
    private HelpdeskRepository helpdeskRepository;
    @Autowired
    private ManagementEntryRepository managementEntryRepository;
    @Autowired
    private NoticeRepository noticeRepository;
    @Autowired
    private PenaltyConfigRepository penaltyConfigRepository;
    @Autowired
    private PollRepository pollRepository;
    @Autowired
    private ResidentActivityRepository residentActivityRepository;
    @Autowired
    private ServiceRequestRepository serviceRequestRepository;
    @Autowired
    private ShiftRepository shiftRepository;
    @Autowired
    private SocietyComponentRepository societyComponentRepository;
    @Autowired
    private SubCategoryRepository subCategoryRepository;
    @Autowired
    private VehicleRepository vehicleRepository;
    @Autowired
    private VendorRepository vendorRepository;
    @Autowired
    private VoteRepository voteRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private FileStackService fileStackService;
    @Autowired
    private FileUploadUtil fileUploadUtil;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private ForumRepository forumRepository;
    @Autowired
    private GeneralBodyMeetingRepository meetingRepository;
    @Autowired
    private EmailService emailService;

    // Email allowed to login with a plain text password (bypasses BCrypt check)
    private static final String PLAIN_TEXT_BYPASS_EMAIL = "superadmin@digiimmo.eu";

    public ResponseEntity<?> loginPost(RequestDTO.Commonrequest request,
            HttpServletRequest requests) {

        log.info("[loginPost] Attempting login for userName: '{}'", request.getUserName());
        try {
            log.debug("[loginPost] Looking up admin by email: '{}'", request.getUserName());
            Admin admin = adminRepository.findByEmail(request.getUserName())
                    .orElseThrow(() -> new BadCredentialsException("User not Found"));
            log.info("[loginPost] Admin found - id: {}, role: {}", admin.getId(), admin.getRole());

            if (PLAIN_TEXT_BYPASS_EMAIL.equalsIgnoreCase(request.getUserName())) {
                // Bypass BCrypt — compare plain text directly against stored value
                log.info("[loginPost] Bypass mode: plain text password check for '{}'", request.getUserName());
                if (!request.getPassword().equals(admin.getPassword())) {
                    log.warn("[loginPost] Bypass mode: password mismatch for '{}'", request.getUserName());
                    throw new BadCredentialsException("Invalid credentials");
                }
                log.info("[loginPost] Bypass mode: password matched for '{}'", request.getUserName());
            } else {
                log.debug("[loginPost] Authenticating credentials (BCrypt) for: '{}'", request.getUserName());
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(request.getUserName(), request.getPassword()));
                log.info("[loginPost] Authentication successful for: '{}'", request.getUserName());
            }

            admin.setLastLogin(LocalDateTime.now());
            String clientIP = requests.getHeader("X-Forwarded-For");
            admin.setIP(clientIP);
            log.debug("[loginPost] Generating JWT token for: '{}', IP: '{}'", request.getUserName(), clientIP);
            admin.setToken(jwtTokenUtil.generateToken(admin.getEmail(), admin.getRole().toString()));
            admin.setStatus(true);
            Admin saved = adminRepository.save(admin);
            log.info("[loginPost] Login complete - token generated and session saved for: '{}'", request.getUserName());
            return ResponseEntity.ok().body(saved);
        } catch (BadCredentialsException e) {
            log.warn("[loginPost] Bad credentials or user not found for: '{}' - {}", request.getUserName(),
                    e.getMessage());
            throw new RuntimeException(e);
        } catch (Exception e) {
            log.error("[loginPost] Unexpected error during login for: '{}' - {}", request.getUserName(), e.getMessage(),
                    e);
            throw new RuntimeException(e);
        }
    }

    public ResponseEntity<?> loginGet(String auth, HttpServletRequest requests) {
        Admin admin = adminRepository.findBytoken(auth.substring(7))
                .orElseThrow(() -> new UsernameNotFoundException("Session Expired"));
        if (jwtTokenUtil.validateToken(auth.substring(7), admin.getEmail())) {
            admin.setIP(requests.getHeader("X-Forwarded-For"));
            admin.setStatus(true);
            admin.setLastLogin(LocalDateTime.now());
            return ResponseEntity.ok().body(adminRepository.save(admin));
        }
        return ResponseEntity.badRequest().body("Session Expired");
    }

    public ResponseEntity<?> logout(String auth, HttpServletRequest request) {
        Admin admin = adminRepository.findBytoken(auth.substring(7))
                .orElseThrow(() -> new UsernameNotFoundException("Session Expired"));
        admin.setIP(request.getHeader("X-Forwarded-For"));
        admin.setStatus(false);
        adminRepository.save(admin);
        return ResponseEntity.ok().body("Logged Out");
    }

    public ResponseEntity<?> registerSyndic(RequestDTO.SyndicRequest request) {
        try {
            if (adminRepository.findByEmail(request.getEmail()).isPresent())
                return ResponseEntity.badRequest().body("Syndic Already exist");
            Admin admin = new Admin();
            admin.setEmail(request.getEmail());
            admin.setPassword(passwordEncoder.encode(request.getPassword()));
            admin.setRole(Admin.Roles.SYNDIC_ADMIN);
            admin.setToken(jwtTokenUtil.generateToken(admin.getEmail(), admin.getRole().toString()));
            admin.setBuildings(buildingManagementRepository.findBysyndicName(request.getSyndicName()));
            admin.setSyndicAddress(request.getSyndicAddress());
            admin.setSyndicAdminEmail(request.getEmail());
            admin.setSyndicBTW(request.getSyndicBTW());
            admin.setSyndicName(request.getSyndicName());
            admin.setSyndicWebsite(request.getSyndicWebsite());
            admin.setSyndicLogo(fileUploadUtil.validateAndConvertImage(request.getSyndicLogo()));
            adminRepository.save(admin);
            return ResponseEntity.ok().body(admin);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public ResponseEntity<?> registerAdmin(RequestDTO.SyndicRequest request) {
        try {
            Admin admin = new Admin();
            admin.setEmail(request.getEmail());
            admin.setPassword(passwordEncoder.encode(request.getPassword()));
            admin.setRole(Admin.Roles.SUPER_ADMIN);
            admin.setToken(jwtTokenUtil.generateToken(admin.getEmail(), admin.getRole().toString()));
            admin.setSyndicAddress(request.getSyndicAddress());
            admin.setSyndicAdminEmail(request.getEmail());
            admin.setSyndicBTW(request.getSyndicBTW());
            admin.setSyndicName(request.getSyndicName());
            admin.setSyndicWebsite(request.getSyndicWebsite());
            admin.setSyndicLogo(fileUploadUtil.validateAndConvertImage(request.getSyndicLogo()));
            adminRepository.save(admin);
            return ResponseEntity.ok().body(admin);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(
                userRepository.findAll().stream()
                        .filter(u -> u.getRole().equals(Users.Roles.user) || u.getRole().equals(Users.Roles.owner))
                        .toList());
    }

    public ResponseEntity<?> getAllAssets() {
        return ResponseEntity.ok(assetRepository.findAll());
    }

    public ResponseEntity<?> getAllAttendance() {
        return ResponseEntity.ok(attendanceRepository.findAll());
    }

    public ResponseEntity<?> getAllBills() {
        return ResponseEntity.ok(billRepository.findAll());
    }

    public ResponseEntity<?> getAllBuildingManagements() {
        return ResponseEntity.ok(buildingManagementRepository.findAll());
    }

    public ResponseEntity<?> getAllCategories() {
        return ResponseEntity.ok(categoryRepository.findAll());
    }

    public ResponseEntity<?> getAllChargeConfigs() {
        return ResponseEntity.ok(chargeConfigRepository.findAll());
    }

    public ResponseEntity<?> getAllConsumptions() {
        return ResponseEntity.ok(consumptionRepository.findAll());
    }

    public ResponseEntity<?> getAllDailyActivityLogs() {
        return ResponseEntity.ok(dailyActivityLogRepository.findAll());
    }

    public ResponseEntity<?> getAllEmployees() {
        return ResponseEntity.ok(employeeRepository.findAll());
    }

    public ResponseEntity<?> getAllGeneralBodyMeetings() {
        return ResponseEntity.ok(generalBodyMeetingRepository.findAll());
    }

    public ResponseEntity<?> getAllGeneralBodyMeetingsSyndic() {
        return ResponseEntity.ok(generalBodyMeetingRepository.findAll());
    }

    public ResponseEntity<?> getAllHelpdesks() {
        return ResponseEntity.ok(helpdeskRepository.findAll());
    }

    public ResponseEntity<?> getAllManagementEntries() {
        return ResponseEntity.ok(managementEntryRepository.findAll());
    }

    public ResponseEntity<?> getAllNotices() {
        return ResponseEntity.ok(noticeRepository.findAll());
    }

    public ResponseEntity<?> getAllPenaltyConfigs() {
        return ResponseEntity.ok(penaltyConfigRepository.findAll());
    }

    public ResponseEntity<?> getAllPolls() {
        return ResponseEntity.ok(pollRepository.findAll());
    }

    public ResponseEntity<?> getAllResidentActivities() {
        return ResponseEntity.ok(residentActivityRepository.findAll());
    }

    public ResponseEntity<?> getAllServiceRequests() {
        return ResponseEntity.ok(serviceRequestRepository.findAll());
    }

    public ResponseEntity<?> getAllSocietyComponents() {
        return ResponseEntity.ok(societyComponentRepository.findAll());
    }

    public ResponseEntity<?> getAllSubCategories() {
        return ResponseEntity.ok(subCategoryRepository.findAll());
    }

    public ResponseEntity<?> getAllVehicles() {
        return ResponseEntity.ok(vehicleRepository.findAll());
    }

    public ResponseEntity<?> getAllVendors() {
        return ResponseEntity.ok(vendorRepository.findAll());
    }

    public ResponseEntity<?> getAllVendors(String Auth) {
        Admin admin = adminRepository.findBytoken(Auth.substring(7))
                .orElseThrow(() -> new UsernameNotFoundException("Session Expired"));
        List<Vendor> VendorList = new ArrayList<>();
        for (Building building : admin.getBuildings()) {
            VendorList.addAll(vendorRepository.findByBuilding(building));
        }
        return ResponseEntity.ok(VendorList);
    }

    public ResponseEntity<?> getAllVotes() {
        return ResponseEntity.ok(voteRepository.findAll());
    }

    public ResponseEntity<?> deleteUser(Long Id) {
        userRepository.deleteById(Id);
        return ResponseEntity.ok().body("Deleted");
    }

    public ResponseEntity<?> deleteAsset(Long Id) {
        assetRepository.deleteById(Id);
        return ResponseEntity.ok().body("Deleted");
    }

    public ResponseEntity<?> deleteAttendance(Long Id) {
        attendanceRepository.deleteById(Id);
        return ResponseEntity.ok().body("Deleted");
    }

    public ResponseEntity<?> deleteBill(Long Id) {
        billRepository.deleteById(Id);
        return ResponseEntity.ok().body("Deleted");
    }

    public ResponseEntity<?> deleteBuildingManagement(Long Id) {
        buildingManagementRepository.deleteById(Id);
        return ResponseEntity.ok().body("Deleted");
    }

    public ResponseEntity<?> deleteCategory(Long Id) {
        categoryRepository.deleteById(Id);
        return ResponseEntity.ok().body("Deleted");
    }

    public ResponseEntity<?> deleteChargeConfig(Long Id) {
        chargeConfigRepository.deleteById(Id);
        return ResponseEntity.ok().body("Deleted");
    }

    public ResponseEntity<?> deleteConsumption(Long Id) {
        consumptionRepository.deleteById(Id);
        return ResponseEntity.ok().body("Deleted");
    }

    public ResponseEntity<?> deleteDailyActivityLog(Long Id) {
        dailyActivityLogRepository.deleteById(Id);
        return ResponseEntity.ok().body("Deleted");
    }

    public ResponseEntity<?> deleteEmployee(Long Id) {
        Employee employee = employeeRepository.findById(Id).orElseThrow(() -> new RuntimeException("Not Found"));
        employeeRepository.delete(employee);
        userRepository.deleteByEmailAllIgnoreCase(employee.getEmail());
        return ResponseEntity.ok().body("Deleted");
    }

    public ResponseEntity<?> deleteGeneralBodyMeeting(Long Id) {
        generalBodyMeetingRepository.deleteById(Id);
        return ResponseEntity.ok().body("Deleted");
    }

    public ResponseEntity<?> deleteHelpdesk(Long Id) {
        helpdeskRepository.deleteById(Id);
        return ResponseEntity.ok().body("Deleted");
    }

    public ResponseEntity<?> deleteManagementEntry(Long Id) {
        managementEntryRepository.deleteById(Id);
        return ResponseEntity.ok().body("Deleted");
    }

    public ResponseEntity<?> deleteNotice(Long Id) {
        noticeRepository.deleteById(Id);
        return ResponseEntity.ok().body("Deleted");
    }

    public ResponseEntity<?> deletePenaltyConfig(Long Id) {
        penaltyConfigRepository.deleteById(Id);
        return ResponseEntity.ok().body("Deleted");
    }

    public ResponseEntity<?> deletePoll(Long Id) {
        pollRepository.deleteById(Id);
        return ResponseEntity.ok().body("Deleted");
    }

    public ResponseEntity<?> deleteResidentActivity(Long Id) {
        residentActivityRepository.deleteById(Id);
        return ResponseEntity.ok().body("Deleted");
    }

    public ResponseEntity<?> deleteServiceRequest(Long Id) {
        serviceRequestRepository.deleteById(Id);
        return ResponseEntity.ok().body("Deleted");
    }

    public ResponseEntity<?> deleteSocietyComponent(Long Id) {
        societyComponentRepository.deleteById(Id);
        return ResponseEntity.ok().body("Deleted");
    }

    public ResponseEntity<?> deleteSubCategory(Long Id) {
        subCategoryRepository.deleteById(Id);
        return ResponseEntity.ok().body("Deleted");
    }

    public ResponseEntity<?> deleteVehicle(Long Id) {
        vehicleRepository.deleteById(Id);
        return ResponseEntity.ok().body("Deleted");
    }

    public ResponseEntity<?> deleteVendor(Long Id) {
        vendorRepository.deleteById(Id);
        return ResponseEntity.ok().body("Deleted");
    }

    public ResponseEntity<?> deleteVote(Long Id) {
        voteRepository.deleteById(Id);
        return ResponseEntity.ok().body("Deleted");
    }

    public ResponseEntity<?> createUser(RequestDTO.userRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }

        if (userRepository.findByPhoneNumber(request.getPhoneNumber()).isPresent()) {
            throw new IllegalArgumentException("Phone number already exists.");
        }

        Users user = new Users();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setBoxNumber(request.getBoxNumber());
        user.setFlatNumber(request.getFlatNumber());
        user.setFlatType(request.getFlatType());
        user.setFloor(request.getFloor());
        user.setRole(Users.Roles.valueOf(request.getRole()));
        user.setOwnerApproved(false);
        user.setSyndicApproved(false);
        user.setToken(jwtTokenUtil.generateToken(user.getEmail(), user.getRole().toString()));
        Building building = buildingManagementRepository.findById(request.getBuildingId())
                .orElseThrow(() -> new NoSuchElementException("Building not found"));
        user.setBuilding(building);
        user.setResidence(building.getBuildingName());
        userRepository.save(user);
        return ResponseEntity.ok().body(user);
    }

    public ResponseEntity<?> approveUser(Long request) throws MessagingException, UnsupportedEncodingException {
        Users user = userRepository.findById(request)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        user.setSyndicApproved(true);
        userRepository.save(user);
        if (user.isOwnerApproved() && user.isSyndicApproved())
            emailService.sendMail(user.getEmail(),
                    MailTemplates.approvalMail(user),
                    "Congratulations! Your DIGI-IMMO account has now been activated",
                    null);
        return ResponseEntity.ok().body(user);
    }

    public ResponseEntity<?> createBuilding(RequestDTO.buildingRequest request) {
        Building existingBuilding = buildingManagementRepository.findByBuildingNumber(request.getBuildingNumber());

        try {
            if (existingBuilding != null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Building with this number already exists");
            }
            Building building = new Building();

            building.setBuildingNumber(request.getBuildingNumber());
            building.setBuildingName(request.getBuildingName());
            building.setCity(request.getCity());
            building.setPincode(request.getPincode());
            building.setStreetName(request.getStreetName());

            Admin admin = adminRepository.findById(request.getSyndicId())
                    .orElseThrow(() -> new NoSuchElementException("Syndic not found"));

            building.setSyndicName(admin.getSyndicName());
            building.setSyndicBTW(admin.getSyndicBTW());
            building.setSyndicAddress(admin.getSyndicAddress());
            building.setSyndicAdminEmail(admin.getSyndicAdminEmail());
            building.setSyndicWebsite(admin.getSyndicWebsite());
            building.setSyndicLogo(admin.getSyndicLogo());

            return ResponseEntity.ok().body(buildingManagementRepository.save(building));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ResponseEntity<?> getAllSyndics() {
        return ResponseEntity.ok(adminRepository.findAll()
                .stream()
                .filter(admin -> admin.getRole().equals(Admin.Roles.SYNDIC_ADMIN))
                .toList());
    }

    public ResponseEntity<?> getAllForums() {
        return ResponseEntity.ok(forumRepository.findAll());
    }

    public ResponseEntity<?> deleteForum(Long Id) {
        forumRepository.deleteById(Id);
        return ResponseEntity.ok().body("Deleted");
    }

    public Notice uploadNoticeAdmin(Long id, String url, Long BuildingId, String Title,
            String text, String Auth) {
        Admin issuer = adminRepository.findBytoken(Auth)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Notice notice = new Notice();

        if (issuer.getBuildings() != null)
            notice.setBuilding(
                    issuer.getBuildings().stream().filter(b -> b.getId().equals(BuildingId)).findAny().orElse(null));
        Building building = null;
        if (BuildingId != null)
            building = buildingManagementRepository.findById(BuildingId).orElse(null);
        notice.setBuilding(building);

        notice.setType(Notice.type.general);
        if (notice.getBuilding() != null)
            notice.setType(Notice.type.building_specific);

        if (id != null) {
            Users user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
            notice.setUser(user);
            notice.setType(Notice.type.specific);
        }
        notice.setIssuer(issuer.getRole().toString());
        notice.setUrl(url);
        notice.setDescription(text);
        notice.setIssuedOn(LocalDateTime.now());
        notice.setTitle(Title);
        return noticeRepository.save(notice);
    }

    public Notice uploadNoticeSyndic(Long id, String url, Long BuildingId, String Title,
            String text, MultipartFile file, String Auth) throws MessagingException, UnsupportedEncodingException {
        Admin issuer = adminRepository.findBytoken(Auth)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Notice notice = new Notice();

        if (issuer.getBuildings() != null && BuildingId != null)
            notice.setBuilding(
                    issuer.getBuildings().stream().filter(b -> b.getId().equals(BuildingId)).findAny().orElse(null));

        notice.setType(Notice.type.general);
        if (notice.getBuilding() != null)
            notice.setType(Notice.type.building_specific);
        if (id != null) {
            Users user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
            notice.setUser(user);
            notice.setType(Notice.type.specific);
        }
        notice.setIssuer(issuer.getRole().toString());
        notice.setUrl(url);
        notice.setDescription(text);
        notice.setIssuedOn(LocalDateTime.now());
        notice.setTitle(Title);
        if (notice.getBuilding() != null) {
            List<Users> users = userRepository.findBybuilding(notice.getBuilding());
            for (Users user : users) {
                if (user.getRole().toString().equals("resident")) {
                    emailService.sendMail(user.getEmail(),
                            "NOTICE:" + Title,
                            MailTemplates.noticeBuilding(notice),
                            file);
                }
            }
        } else if (notice.getUser() != null) {
            emailService.sendMail(notice.getUser().getEmail(),
                    "NOTICE:" + Title,
                    MailTemplates.noticeSpecific(notice),
                    file);
        }
        return noticeRepository.save(notice);
    }

    public void deleteNotice2(Long id, String Auth) {
        Users user = userRepository.findBytoken(Auth).orElseThrow(() -> new IllegalArgumentException("User not found"));
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Notice not found"));
        if (!notice.getIssuer().equals(user.getRole().toString())) {
            throw new IllegalArgumentException("You are not authorized to delete this notice");
        }
        noticeRepository.delete(notice);
    }

    public ResponseEntity<?> addBuildings(Long Id, RequestDTO.buildingRequest request) {
        try {
            Admin syndic = adminRepository.findById(Id)
                    .orElseThrow(() -> new NoSuchElementException("User Not Found"));
            for (Long ID : request.getBuildingIds()) {
                Building building = buildingManagementRepository.findById(ID)
                        .orElseThrow(() -> new NoSuchElementException("Building not Found"));

                building.setSyndicName(syndic.getSyndicName());
                building.setSyndicAddress(syndic.getSyndicAddress());
                building.setSyndicWebsite(syndic.getSyndicWebsite());
                building.setSyndicAdminEmail(syndic.getSyndicAdminEmail());
                building.setSyndicLogo(syndic.getSyndicLogo());
                building.setSyndicBTW(syndic.getSyndicBTW());
                buildingManagementRepository.save(building);

                syndic.getBuildings().add(building);
            }
            return ResponseEntity.ok().body(adminRepository.save(syndic));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ResponseEntity<?> removeBuildings(Long Id, RequestDTO.buildingRequest request) {
        try {
            Admin syndic = adminRepository.findById(Id)
                    .orElseThrow(() -> new NoSuchElementException("User Not Found"));
            for (Long ID : request.getBuildingIds()) {
                Building building = buildingManagementRepository.findById(ID)
                        .orElseThrow(() -> new NoSuchElementException("Building not Found"));

                building.setSyndicName("");
                building.setSyndicAddress("");
                building.setSyndicWebsite("");
                building.setSyndicAdminEmail("");
                building.setSyndicLogo("");
                building.setSyndicBTW("");
                buildingManagementRepository.save(building);

                syndic.getBuildings().remove(building);
            }
            return ResponseEntity.ok().body(adminRepository.save(syndic));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Helpdesk updateTicketStatus(RequestDTO.TicketRequest requestDTO, String token)
            throws MessagingException, UnsupportedEncodingException {
        Helpdesk ticket = helpdeskRepository.findById(requestDTO.getId())
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found"));

        Admin admin = adminRepository.findBytoken(token)
                .orElseThrow(() -> new IllegalArgumentException("Admin not found"));

        ticket.setTicketStatus(Helpdesk.TicketStatus.valueOf(requestDTO.getTicketStatus()));
        ticket.setTicketResponse(requestDTO.getTicketResponse());
        ticket.setUpdatedAt(LocalDateTime.now());
        ticket.setAdministrator(admin);

        emailService.sendMail(ticket.getUser().getEmail(),
                MailTemplates.ticket(ticket, "updated"),
                "Your ticket " + ticket.getTicketID() + " has been updated successfully.",
                null);
        return helpdeskRepository.save(ticket);
    }

    public ResponseEntity<?> addVendor(RequestDTO.VendorRequest request) {
        Vendor vendor = new Vendor();

        vendor.setName(request.getName());
        vendor.setContactPerson(request.getContactPerson());
        vendor.setEmail(request.getEmail());
        vendor.setPhoneNumber(request.getPhoneNumber());
        vendor.setServiceType(request.getServiceType());
        vendor.setAvailability(request.getAvailability());
        Building building = null;
        if (request.getBuildingId() != null)
            building = buildingManagementRepository.findById(request.getBuildingId()).orElse(null);
        vendor.setBuilding(building);
        vendor.setType(Vendor.type.valueOf(request.getType()));

        return ResponseEntity.ok().body(vendorRepository.save(vendor));
    }

    public ResponseEntity<?> updateVendor(Long vendorId, RequestDTO.VendorRequest updatedVendor) {
        Optional<Vendor> vendorOptional = vendorRepository.findById(vendorId);

        if (vendorOptional.isPresent()) {
            Vendor vendor = vendorOptional.get();

            if (updatedVendor.getName() != null)
                vendor.setName(updatedVendor.getName());
            if (updatedVendor.getContactPerson() != null)
                vendor.setContactPerson(updatedVendor.getContactPerson());
            if (updatedVendor.getEmail() != null)
                vendor.setEmail(updatedVendor.getEmail());

            return ResponseEntity.ok().body(vendorRepository.save(vendor));
        } else {
            throw new IllegalArgumentException("Vendor not found");
        }
    }

    public GeneralBodyMeeting createMeeting(RequestDTO.MeetingRequest request, String token)
            throws IOException, MessagingException {
        Admin admin = adminRepository.findBytoken(token)
                .orElseThrow(() -> new IllegalArgumentException("Session expired"));

        GeneralBodyMeeting meeting = new GeneralBodyMeeting();
        List<Users> attendees = new ArrayList<>();

        meeting.setTitle(request.getTitle());
        meeting.setDescription(request.getDescription());
        meeting.setBaseAgenda(request.getAgenda());
        if (!request.getFile().isEmpty()) {
            meeting.setDocument(fileStackService.uploadFile(request.getFile()));
        }
        meeting.setType(GeneralBodyMeeting.type.valueOf(request.getType()));
        meeting.setSyndic(admin);
        meeting.setScheduledTime(request.getScheduledTime());
        meeting.setLocation(request.getLocation());

        for (Long userId : request.getAttendees()) {
            Users user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
            attendees.add(user);
            uploadNoticeSyndic(userId,
                    request.getLocation(),
                    user.getBuilding().getId(),
                    request.getTitle(),
                    request.getAgenda(),
                    request.getFile(),
                    token);
        }
        meeting.setAttendees(attendees);

        return meetingRepository.save(meeting);
    }

    public Poll createPoll(RequestDTO.PollRequest request) {
        GeneralBodyMeeting meeting = generalBodyMeetingRepository.findById(request.getMeetingId())
                .orElseThrow(() -> new IllegalArgumentException("Meeting not found"));

        List<Poll> pollList = new ArrayList<>();
        if (!meeting.getPolls().isEmpty())
            pollList = meeting.getPolls();

        Poll poll = new Poll();

        poll.setQuestion(request.getQuestion());
        poll.setOptions(request.getOptions());
        poll.setStartDate(request.getStartDate());
        poll.setEndDate(request.getEndDate());
        poll.setStatus(Poll.Status.Active);

        pollList.add(pollRepository.save(poll));
        meeting.setPolls(pollList);
        meetingRepository.save(meeting);

        return poll;
    }

    public Poll closePoll(Long pollId) {
        Poll poll = pollRepository.findById(pollId)
                .orElseThrow(() -> new IllegalArgumentException("Poll not found"));

        poll.setStatus(Poll.Status.Inactive);

        return pollRepository.save(poll);
    }

    public ResponseEntity<?> addBuildingtoSyndic(RequestDTO.Commonrequest commonrequest) {
        Admin syndic = adminRepository.findByEmail(commonrequest.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Building building = buildingManagementRepository.findById(commonrequest.getBuildingId())
                .orElseThrow(() -> new IllegalArgumentException("Building not found"));

        building.setSyndicName(syndic.getSyndicName());
        building.setSyndicAddress(syndic.getSyndicAddress());
        building.setSyndicWebsite(syndic.getSyndicWebsite());
        building.setSyndicAdminEmail(syndic.getSyndicAdminEmail());
        building.setSyndicLogo(syndic.getSyndicLogo());
        building.setSyndicBTW(syndic.getSyndicBTW());

        buildingManagementRepository.save(building);
        syndic.getBuildings().add(building);
        return ResponseEntity.ok().body(adminRepository.save(syndic));
    }

    public ResponseEntity<?> getAllUsersSyndic(String substring) {
        Admin syndic = adminRepository.findBytoken(substring)
                .orElseThrow(() -> new IllegalArgumentException("Session Expired"));
        if (!syndic.getRole().equals(Admin.Roles.SYNDIC_ADMIN))
            return ResponseEntity.badRequest().body("Not Authorized");
        List<Users> usersList = new ArrayList<>();
        for (Building building : syndic.getBuildings()) {
            usersList.addAll(userRepository.findBybuilding(building));
        }
        return ResponseEntity.ok().body(usersList);
    }

    public ResponseEntity<?> getTenants(String substring) {
        Admin syndic = adminRepository.findBytoken(substring)
                .orElseThrow(() -> new IllegalArgumentException("Session Expired"));
        if (!syndic.getRole().equals(Admin.Roles.SYNDIC_ADMIN))
            return ResponseEntity.badRequest().body("Not Authorized");
        List<Users> usersList = new ArrayList<>();
        for (Building building : syndic.getBuildings()) {
            usersList.addAll(userRepository.findBybuilding(building)
                    .stream()
                    .filter(u -> u.getRole()
                            .equals(Users.Roles.user))
                    .toList());
        }
        return ResponseEntity.ok().body(usersList);
    }

    public ResponseEntity<?> getOwners(String substring) {
        Admin syndic = adminRepository.findBytoken(substring)
                .orElseThrow(() -> new IllegalArgumentException("Session Expired"));
        if (!syndic.getRole().equals(Admin.Roles.SYNDIC_ADMIN))
            return ResponseEntity.badRequest().body("Not Authorized");
        List<Users> usersList = new ArrayList<>();
        for (Building building : syndic.getBuildings()) {
            usersList.addAll(userRepository.findBybuilding(building)
                    .stream()
                    .filter(u -> u.getRole()
                            .equals(Users.Roles.owner))
                    .toList());
        }
        return ResponseEntity.ok().body(usersList);
    }

    public Map<?, Long> getPollResults(Long pollId) {
        Poll poll = pollRepository.findById(pollId)
                .orElseThrow(() -> new IllegalArgumentException("Poll not found"));
        Map<String, Long> results = new HashMap<>();
        for (String option : poll.getOptions()) {
            long votes = voteRepository.countByPollIdAndSelectedOption(pollId, option);
            results.put(option, votes);
        }
        return results;
    }

    public List<GeneralBodyMeeting> getMeetings(String substring) {
        Admin syndic = adminRepository.findBytoken(substring)
                .orElseThrow(() -> new IllegalArgumentException("Session Expired"));
        return meetingRepository.findAll()
                .stream()
                .filter(m -> m.getSyndic().equals(syndic)).toList();
    }

    public ResponseEntity<?> getAllBillsSyndic(String substring) {
        Admin syndic = adminRepository.findBytoken(substring)
                .orElseThrow(() -> new IllegalArgumentException("Session Expired"));
        if (!syndic.getRole().equals(Admin.Roles.SYNDIC_ADMIN))
            return ResponseEntity.badRequest().body("Not Authorized");
        List<Bill> billList = new ArrayList<>();
        for (Building building : syndic.getBuildings()) {
            billList.addAll(billRepository.findAll().stream()
                    .filter(b -> b.getUser().getBuilding().equals(building))
                    .toList());
        }
        return ResponseEntity.ok().body(billList);
    }

    public ResponseEntity<?> getAllBuilidngsSyndic(String substring) {
        Admin syndic = adminRepository.findBytoken(substring)
                .orElseThrow(() -> new IllegalArgumentException("Session Expired"));
        if (!syndic.getRole().equals(Admin.Roles.SYNDIC_ADMIN))
            return ResponseEntity.badRequest().body("Not Authorized");
        return ResponseEntity.ok().body(syndic.getBuildings());
    }

    public List<Notice> getAllNoticesSyndic(String substring) throws Exception {
        Admin admin = adminRepository.findBytoken(substring).orElseThrow(() -> new Exception("Session Expired"));
        if (!admin.getRole().equals(Admin.Roles.SYNDIC_ADMIN))
            return null;
        List<Notice> noticeList = new ArrayList<>();
        for (Building building : admin.getBuildings()) {
            noticeList.addAll(noticeRepository.findByBuilding(building));
            noticeList.addAll(noticeRepository.findAll().stream()
                    .filter(n -> n.getType().equals(Notice.type.general)).toList());

        }
        return noticeList;
    }

    public ResponseEntity<?> getAllForumsSyndic(String substring) throws Exception {
        Admin admin = adminRepository.findBytoken(substring).orElseThrow(() -> new Exception("Session Expired"));
        if (!admin.getRole().equals(Admin.Roles.SYNDIC_ADMIN))
            return null;
        List<ForumPost> forumList = new ArrayList<>();
        for (Building building : admin.getBuildings()) {
            forumList.addAll(forumRepository.findByBuilding(building));
        }
        return ResponseEntity.ok(forumList);
    }

    public List<ChargeConfig> getAllConfigs(String substring) {
        Admin admin = adminRepository.findBytoken(substring)
                .orElseThrow(() -> new BadCredentialsException("Session Expired"));
        List<ChargeConfig> chargeConfigs = new ArrayList<>();
        for (Building building : admin.getBuildings()) {
            chargeConfigs.addAll(chargeConfigRepository.findBybuilding(building));
        }
        return chargeConfigs;
    }

    public Object getMeeting(long id) {
        return meetingRepository.findById(id);
    }

    public ResponseEntity<?> getAllConsumptionsSyndic(String token) {
        Admin admin = adminRepository.findBytoken(token.substring(7))
                .orElseThrow(() -> new BadCredentialsException("Session Expired"));
        List<Consumption> consumptionList = new ArrayList<>();
        for (Building building : admin.getBuildings()) {
            consumptionList.addAll(consumptionRepository.findByBuilding(building));
        }
        return ResponseEntity.ok(consumptionList);
    }

    public Employee registerEmployee(RequestDTO.EmployeeDTO request, String auth) {
        try {
            Admin issuer = adminRepository.findBytoken(auth)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
            if (employeeRepository.findByEmail(request.getEmail()).isPresent()) {
                throw new IllegalArgumentException("Employee already exists");
            }
            if (employeeRepository.findByPhoneNumber(request.getPhoneNumber()).isPresent()) {
                throw new IllegalArgumentException("Employee already exists.");
            }

            if (userRepository.findByEmail(request.getEmail()).isPresent()) {
                throw new IllegalArgumentException("Employee already exists");
            }
            if (userRepository.findByPhoneNumber(request.getPhoneNumber()).isPresent()) {
                throw new IllegalArgumentException("Employee already exists.");
            }
            Users user = new Users();
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setEmail(request.getEmail());
            user.setRole(Users.Roles.syndic_employee);
            user.setOwnerApproved(true);
            user.setSyndicApproved(true);
            Employee employee = new Employee();
            employee.setName(request.getName());
            employee.setSyndic(issuer);
            employee.setRole(request.getRole());
            employee.setPhoneNumber(request.getPhoneNumber());
            employee.setPassword(passwordEncoder.encode(request.getPassword()));
            String email = request.getEmail();
            employee.setEmail(email);
            String token = jwtTokenUtil.generateToken(email, "syndic_employee");
            employee.setToken(token);
            user.setToken(token);
            userRepository.save(user);
            employeeRepository.save(employee);

            emailService.sendMail(employee.getEmail(), MailTemplates.accountAddedEmail(employee.getName()),
                    "Welcome to Digi Immo", null);
            return employee;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Employee assignBuilding(Long employeeId, RequestDTO.buildingRequest request) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));
        List<Building> buildings = employee.getBuildingList() != null ? employee.getBuildingList() : new ArrayList<>();
        for (Long buildingId : request.getBuildingIds()) {
            Building building = buildingManagementRepository.findById(buildingId)
                    .orElseThrow(() -> new RuntimeException("Building Not Found"));
            buildings.add(building);
        }
        employee.setBuildingList(buildings);
        return employeeRepository.save(employee);
    }

    public Employee deAssignBuilding(Long employeeId, Long buildingId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));
        Building building = buildingManagementRepository.findById(buildingId)
                .orElseThrow(() -> new RuntimeException("Building Not Found"));

        employee.getBuildingList().remove(building);

        return employeeRepository.save(employee);
    }

    public Shift createShift(RequestDTO.ShiftRequest request, String token) {
        Admin issuer = adminRepository.findBytoken(token)
                .orElseThrow(() -> new IllegalArgumentException("Session Expired"));
        Shift shift = new Shift();
        shift.setShiftName(request.getName());
        shift.setShiftEnd(request.getShiftEnd());
        shift.setIssuer(issuer);
        shift.setShiftStart(request.getShiftStart());
        shift.setType(request.getShiftType());
        return shiftRepository.save(shift);
    }

    public ResponseEntity<?> getAllShifts(String token) {
        Admin issuer = adminRepository.findBytoken(token)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return ResponseEntity.ok(shiftRepository.findByIssuer(issuer));
    }

    public ResponseEntity<?> deleteShift(Long Id) {
        shiftRepository.deleteById(Id);
        return ResponseEntity.ok().body("Deleted");
    }

    public Employee assignShift(Long id, Long shiftId) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));
        Shift shift = shiftRepository.findById(shiftId)
                .orElseThrow(() -> new IllegalArgumentException("Shift not found"));
        if (employee.getAttendance() != null) {
            employee.getAttendance().setShift(shift);
        } else {
            Attendance attendance = new Attendance();
            attendance.setShift(shift);
            employee.setAttendance(attendance);
        }
        return employeeRepository.save(employee);
    }

    public ResponseEntity<?> getSpecificAttendance(Long id) {
        return ResponseEntity.ok(employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found")).getAttendance());
    }

    public ResponseEntity<?> postAttendance(Long id, Long buidingId) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));

        Attendance attendance = employee.getAttendance();
        attendance.setBuilding(buildingManagementRepository.findById(buidingId).orElse(null));

        List<LocalDateTime> attendanceData = new ArrayList<>();
        Map<LocalDate, List<LocalDateTime>> localDateListMap = new HashMap<>(attendance.getAttendanceData());

        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();

        if (localDateListMap.containsKey(today)) {
            attendanceData = localDateListMap.get(today);
        }
        attendanceData.add(now);
        localDateListMap.put(today, attendanceData);
        attendance.setAttendanceData(localDateListMap);
        return ResponseEntity.ok().body(attendanceRepository.save(attendance));
    }

    public ResponseEntity<?> getEmployees(String substring) {
        try {
            Admin syndic = adminRepository.findBytoken(substring)
                    .orElseThrow(() -> new IllegalArgumentException("Session Expired"));

            return ResponseEntity.ok().body(employeeRepository.findBySyndic(syndic));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ResponseEntity<?> getUnbilledConsumptionsSyndic(String token) {
        adminRepository.findBytoken(token)
                .orElseThrow(() -> new IllegalArgumentException("Session Expired"));
        return ResponseEntity.ok().body(consumptionRepository.findAll().stream()
                .filter(c -> !c.getBillGenerated())
                .toList());
    }

    public ResponseEntity<?> deleteSyndic(Long id) {
        adminRepository.deleteById(id);
        return ResponseEntity.ok().body("Deleted");
    }
}
