package com.vunum.SocietyAdmin.Service;

import com.vunum.SocietyAdmin.DTO.RequestDTO;
import com.vunum.SocietyAdmin.Utilities.EmailService;
import com.vunum.SocietyAdmin.Utilities.MailTemplates;
import com.vunum.SocietyAdmin.Utilities.PdfGenerator;
import com.vunum.SocietyAdmin.entity.*;
import com.vunum.SocietyAdmin.repository.BillRepository;
import com.vunum.SocietyAdmin.repository.ChargeConfigRepository;
import com.vunum.SocietyAdmin.repository.ConsumptionRepository;
import com.vunum.SocietyAdmin.repository.UserRepository;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ChargeCalculationService {

    @Autowired
    private PdfGenerator pdfGenerator;

    @Autowired
    private ChargeConfigRepository chargeConfigRepository;

    @Autowired
    private ConsumptionRepository consumptionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private MailTemplates templates;

    public void calculateAndGenerateBills(List<Long> consumptionIdList, LocalDate dueDate,
                                          String billingMonth) throws MessagingException, IOException {

        Double totalElectricityCharge;
        Double totalGasCharge;
        Double totalReserveCharge;
        Double totalwaterCharge;
        int times = 0;

        List<Consumption> consumptions = consumptionRepository
                .findAllById(consumptionIdList)
                .stream()
                .filter(c -> c.getSource()
                        .equals(Consumption.source.RESIDENCE) && !c.getBillGenerated())
                .toList();
        if (consumptions.isEmpty()) throw new RuntimeException("No consumptions found");
        Set<Users> usersToInvoice = consumptions.stream()
                .map(Consumption::getUser)
                .collect(Collectors.toSet());


        for (Consumption consumption : consumptions) {
            Users user = consumption.getUser();
            List<ChargeConfig> chargeConfig = chargeConfigRepository.findBybuildingAndResidentTypeAndFlatType(
                            user.getBuilding(),
                            user.getRole().toString(),
                            user.getFlatType())
                    .orElseThrow(() -> new IllegalArgumentException("ChargeConfig not found"));


            ChargeConfig chargeConfigSelect;
            if (consumption.getType().equals(Consumption.type.CUSTOM)) {
                ChargeConfig chargeConfig3 = chargeConfig.stream().filter(c -> c.getType()
                        .equals(ChargeConfig.type.CUSTOM)).findAny().orElseThrow(() ->
                        new RuntimeException("Config not found"));

                //Handle Custom Utility Charges
                Map<String, Double> utilityCharges = chargeConfig3.getCustomUtilities();
                for (Map.Entry<String, Double> entry : utilityCharges.entrySet()) {
                    if (entry.getValue() > 0) {
                        Bill bill = new Bill();
                        bill.setUser(user);
                        bill.setBillId(String.valueOf(UUID.randomUUID()));
                        bill.setBillingMonth(billingMonth);
                        bill.setTotalAmount(consumption.getUnitsConsumed() * entry.getValue() + chargeConfig3.getMaintenanceRate());
                        bill.setType(Bill.type.UTILITIES);
                        bill.setBillName(entry.getKey());
                        bill.setStatus(Bill.status.UNPAID);
                        bill.setDueDate(dueDate);
                        bill.setConsumption(consumption);
                        billRepository.save(bill);
                    }
                }
            }

            if (consumption.getType().equals(Consumption.type.WATER)) {
                chargeConfigSelect = chargeConfig.stream().filter(c -> c.getType()
                        .equals(ChargeConfig.type.WATER)).findAny().orElseThrow(() ->
                        new RuntimeException("Water config not found"));
                totalwaterCharge = consumption.getUnitsConsumed() * chargeConfigSelect.getBaseRate() + chargeConfigSelect.getMaintenanceRate();

                if (totalwaterCharge > 0) {
                    Bill waterBill = new Bill();
                    waterBill.setUser(user);
                    waterBill.setBillId(String.valueOf(UUID.randomUUID()));
                    waterBill.setBillingMonth(billingMonth);
                    waterBill.setTotalAmount(totalwaterCharge);
                    waterBill.setType(Bill.type.PROVISIONS);
                    waterBill.setBillName("Water Bill");
                    waterBill.setStatus(Bill.status.UNPAID);
                    waterBill.setDueDate(dueDate);
                    waterBill.setConsumption(consumption);
                    billRepository.save(waterBill);
                }

            }

            if (consumption.getType().equals(Consumption.type.HEATING)) {
                chargeConfigSelect = chargeConfig.stream().filter(c -> c.getType()
                        .equals(ChargeConfig.type.HEATING)).findAny().orElseThrow(() ->
                        new RuntimeException("Heating config not found"));
                totalGasCharge = consumption.getUnitsConsumed() * chargeConfigSelect.getBaseRate() + chargeConfigSelect.getMaintenanceRate();

                if (totalGasCharge > 0) {
                    Bill gasBill = new Bill();
                    gasBill.setUser(user);
                    gasBill.setBillId(String.valueOf(UUID.randomUUID()));
                    gasBill.setBillingMonth(billingMonth);
                    gasBill.setTotalAmount(totalGasCharge);
                    gasBill.setType(Bill.type.PROVISIONS);
                    gasBill.setBillName("Heating Bill");
                    gasBill.setStatus(Bill.status.UNPAID);
                    gasBill.setDueDate(dueDate);
                    gasBill.setConsumption(consumption);
                    billRepository.save(gasBill);
                }
            }

            if (consumption.getType().equals(Consumption.type.ELECTRICITY)) {
                chargeConfigSelect = chargeConfig.stream().filter(c -> c.getType()
                        .equals(ChargeConfig.type.ELECTRICITY)).findAny().orElseThrow(() ->
                        new RuntimeException("Electricity not found"));
                Double baseCharge = consumption.getUnitsConsumed() * chargeConfigSelect.getBaseRate() + chargeConfigSelect.getMaintenanceRate();
                Double maintenanceCharge = chargeConfigSelect.getMaintenanceRate();
                totalElectricityCharge = baseCharge + maintenanceCharge;

                if (totalElectricityCharge > 0) {
                    Bill electricityBill = new Bill();
                    electricityBill.setUser(user);
                    electricityBill.setBillId(String.valueOf(UUID.randomUUID()));
                    electricityBill.setBillingMonth(billingMonth);
                    electricityBill.setTotalAmount(totalElectricityCharge);
                    electricityBill.setType(Bill.type.PROVISIONS);
                    electricityBill.setBillName("Electricity Bill");
                    electricityBill.setStatus(Bill.status.UNPAID);
                    electricityBill.setDueDate(dueDate);
                    electricityBill.setConsumption(consumption);
                    billRepository.save(electricityBill);
                }
            }
            ChargeConfig reserveConfig;
            try {
                reserveConfig = chargeConfig.stream()
                        .filter(c -> c.getType().equals(ChargeConfig.type.RESERVE)).toList()
                        .getFirst();
            } catch (Exception e) {
                reserveConfig = null;
            }
            // Handle reserve charges based on flat type
            if (reserveConfig != null) {
                totalReserveCharge = reserveConfig.getBaseRate();

                if (totalReserveCharge != null && totalReserveCharge > 0 && times < 1) {
                    times++;
                    Bill reserveChargeBill = new Bill();
                    reserveChargeBill.setUser(user);
                    reserveChargeBill.setBillId(String.valueOf(UUID.randomUUID()));
                    reserveChargeBill.setBillingMonth(billingMonth);
                    reserveChargeBill.setTotalAmount(totalReserveCharge);
                    reserveChargeBill.setType(Bill.type.RESERVE_FUND);
                    reserveChargeBill.setBillName("Reserve Fund Charge");
                    reserveChargeBill.setStatus(Bill.status.UNPAID);
                    reserveChargeBill.setDueDate(dueDate);
                    reserveChargeBill.setConsumption(null);
                    billRepository.save(reserveChargeBill);
                }
            }
            consumption.setBillGenerated(true);
            consumptionRepository.save(consumption);
        }
        for (Users user : usersToInvoice) {
            SendInvoice(user, billingMonth);
        }
    }


    public void calculateAndGenerateGarageBills(List<Long> consumptionIdList, LocalDate dueDate,
                                                String billingMonth, Double ReserveCharge) throws MessagingException, IOException {

        Double totalElectricityCharge;
        Double totalGasCharge;
        Double totalReserveCharge;
        Double totalwaterCharge;
        int times = 0;

        List<Consumption> consumptions = consumptionRepository
                .findAllById(consumptionIdList)
                .stream()
                .filter(c -> c.getSource()
                        .equals(Consumption.source.GARAGE) && !c.getBillGenerated())
                .toList();
        if (consumptions.isEmpty()) throw new RuntimeException("No consumptions found");
        Set<Users> usersToInvoice = consumptions.stream()
                .map(Consumption::getUser)
                .collect(Collectors.toSet());


        for (Consumption consumption : consumptions) {
            Users user = consumption.getUser();
            List<ChargeConfig> chargeConfig = chargeConfigRepository.findBybuildingAndResidentTypeAndFlatType(
                            user.getBuilding(),
                            user.getRole().toString(),
                            "Garage")
                    .orElseThrow(() -> new IllegalArgumentException("ChargeConfig not found"));


            ChargeConfig chargeConfigSelect;
            if (consumption.getType().equals(Consumption.type.CUSTOM)) {
                ChargeConfig chargeConfig3 = chargeConfig.stream().filter(c -> c.getType()
                        .equals(ChargeConfig.type.CUSTOM)).findAny().orElseThrow(() ->
                        new RuntimeException("Config not found"));

                //Handle Custom Utility Charges
                Map<String, Double> utilityCharges = chargeConfig3.getCustomUtilities();
                for (Map.Entry<String, Double> entry : utilityCharges.entrySet()) {
                    if (entry.getValue() > 0) {
                        Bill bill = new Bill();
                        bill.setUser(user);
                        bill.setBillId(String.valueOf(UUID.randomUUID()));
                        bill.setBillingMonth(billingMonth);
                        bill.setTotalAmount(consumption.getUnitsConsumed() * entry.getValue() + chargeConfig3.getMaintenanceRate());
                        bill.setType(Bill.type.UTILITIES);
                        bill.setBillName(entry.getKey());
                        bill.setStatus(Bill.status.UNPAID);
                        bill.setDueDate(dueDate);
                        bill.setConsumption(consumption);
                        billRepository.save(bill);
                    }
                }
            }

            if (consumption.getType().equals(Consumption.type.WATER)) {
                chargeConfigSelect = chargeConfig.stream().filter(c -> c.getType()
                        .equals(ChargeConfig.type.WATER)).findAny().orElseThrow(() ->
                        new RuntimeException("Water config not found"));
                Double waterCharge = consumption.getUnitsConsumed() * chargeConfigSelect.getBaseRate();
                totalwaterCharge = waterCharge + chargeConfigSelect.getMaintenanceRate();

                if (totalwaterCharge > 0) {
                    Bill waterBill = new Bill();
                    waterBill.setUser(user);
                    waterBill.setBillId(String.valueOf(UUID.randomUUID()));
                    waterBill.setBillingMonth(billingMonth);
                    waterBill.setTotalAmount(totalwaterCharge);
                    waterBill.setType(Bill.type.PROVISIONS);
                    waterBill.setBillName("Water Bill");
                    waterBill.setStatus(Bill.status.UNPAID);
                    waterBill.setDueDate(dueDate);
                    waterBill.setConsumption(consumption);
                    billRepository.save(waterBill);
                }

            }

            if (consumption.getType().equals(Consumption.type.HEATING)) {
                chargeConfigSelect = chargeConfig.stream().filter(c -> c.getType()
                        .equals(ChargeConfig.type.HEATING)).findAny().orElseThrow(() ->
                        new RuntimeException("Heating config not found"));
                Double gasCharge = consumption.getUnitsConsumed() * chargeConfigSelect.getBaseRate();
                totalGasCharge = gasCharge + chargeConfigSelect.getMaintenanceRate();

                if (totalGasCharge > 0) {
                    Bill gasBill = new Bill();
                    gasBill.setUser(user);
                    gasBill.setBillId(String.valueOf(UUID.randomUUID()));
                    gasBill.setBillingMonth(billingMonth);
                    gasBill.setTotalAmount(totalGasCharge);
                    gasBill.setType(Bill.type.PROVISIONS);
                    gasBill.setBillName("Heating Bill");
                    gasBill.setStatus(Bill.status.UNPAID);
                    gasBill.setDueDate(dueDate);
                    gasBill.setConsumption(consumption);
                    billRepository.save(gasBill);
                }
            }

            if (consumption.getType().equals(Consumption.type.ELECTRICITY)) {
                chargeConfigSelect = chargeConfig.stream().filter(c -> c.getType()
                        .equals(ChargeConfig.type.ELECTRICITY)).findAny().orElseThrow(() ->
                        new RuntimeException("Electricity not found"));
                Double baseCharge = consumption.getUnitsConsumed() * chargeConfigSelect.getBaseRate();
                totalElectricityCharge = baseCharge + chargeConfigSelect.getMaintenanceRate();

                if (totalElectricityCharge > 0) {
                    Bill electricityBill = new Bill();
                    electricityBill.setUser(user);
                    electricityBill.setBillId(String.valueOf(UUID.randomUUID()));
                    electricityBill.setBillingMonth(billingMonth);
                    electricityBill.setTotalAmount(totalElectricityCharge);
                    electricityBill.setType(Bill.type.PROVISIONS);
                    electricityBill.setBillName("Electricity Bill");
                    electricityBill.setStatus(Bill.status.UNPAID);
                    electricityBill.setDueDate(dueDate);
                    electricityBill.setConsumption(consumption);
                    billRepository.save(electricityBill);
                }
            }

            // Handle reserve charges based on flat type
            totalReserveCharge = ReserveCharge;

            if (totalReserveCharge != null && totalReserveCharge > 0 && times < 1) {
                times++;
                Bill reserveChargeBill = new Bill();
                reserveChargeBill.setUser(user);
                reserveChargeBill.setBillId(String.valueOf(UUID.randomUUID()));
                reserveChargeBill.setBillingMonth(billingMonth);
                reserveChargeBill.setTotalAmount(totalReserveCharge);
                reserveChargeBill.setType(Bill.type.RESERVE_FUND);
                reserveChargeBill.setBillName("Reserve Fund Charge");
                reserveChargeBill.setStatus(Bill.status.UNPAID);
                reserveChargeBill.setDueDate(dueDate);
                reserveChargeBill.setConsumption(null);
                billRepository.save(reserveChargeBill);
            }

            consumption.setBillGenerated(true);
            consumptionRepository.save(consumption);
        }

        for (Users user : usersToInvoice) {
            SendInvoice(user, billingMonth);
        }
    }

    public Consumption insertConsumption(RequestDTO.BillRequest request) {
        Consumption consumption = new Consumption();
        Users user = userRepository.findById(request.getUserId())
                .orElseThrow(()
                        -> new UsernameNotFoundException("User not found"));

        consumption.setUser(user);
        consumption.setBuilding(user.getBuilding());
        consumption.setUnitsConsumed(request.getUnitsConsumed());
        consumption.setType(Consumption.type.valueOf(request.getType()));
        consumption.setBillingMonth(request.getBillingMonth());
        consumption.setBillGenerated(false);
        consumption.setSource(Consumption.source.valueOf(request.getSource()));

        return consumptionRepository.save(consumption);
    }


    public ChargeConfig addConfig(RequestDTO.BillRequest request, Building building) {
        ChargeConfig config = chargeConfigRepository
                .findBybuildingAndType(building, ChargeConfig.type.valueOf(request.getType()))
                .orElse(new ChargeConfig());

        config.setResidentType(request.getResidentType());
        config.setFlatType(request.getFlatType());
        config.setBaseRate(request.getBaseRate());
        config.setMaintenanceRate(request.getMaintenanceRate());
        config.setType(ChargeConfig.type.valueOf(request.getType()));
        config.setBuilding(building);

        if (request.getCustomUtils() != null) config.setCustomUtilities(request.getCustomUtils());

        return chargeConfigRepository.save(config);
    }


    @Async
    private void SendInvoice(Users user, String billingMonth) throws MessagingException, IOException {
        List<Bill> userBills = billRepository.findByUserAndBillingMonth(user, billingMonth)
                .stream()
                .filter(b -> b.getStatus() == Bill.status.UNPAID &&
                        b.getDueDate() != null &&
                        b.getDueDate().getMonth() == LocalDate.now().getMonth() &&
                        b.getDueDate().getYear() == LocalDate.now().getYear())
                .toList();

        emailService.sendMail(user.getEmail(),
                templates.generateInvoiceHtml(userBills), "Residential Bills",
                pdfGenerator.pdfgen(userBills));
    }

    public Object getConfig(Building building) {
        return chargeConfigRepository.findBybuilding(building);
    }
}