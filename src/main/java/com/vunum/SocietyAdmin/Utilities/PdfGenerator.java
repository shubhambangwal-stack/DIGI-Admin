package com.vunum.SocietyAdmin.Utilities;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.vunum.SocietyAdmin.entity.Bill;
import com.vunum.SocietyAdmin.entity.Users;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.List;

import static java.io.File.createTempFile;

@Component
public class PdfGenerator {

    private static String getHeader(String title) {

        return String.format(
                """
                                <div style="text-align: left;">
                                <img src="https://res.cloudinary.com/dvp0fow8r/image/upload/v1737528662/utu1xwubwte52bbjproh.png"
                        alt="Logo" style="max-width: 100px; height: auto; margin-bottom: 10px;">
                                </div>
                        <div style='background-color:#f8f9fa;
                        padding:20px 0;
                        "text-align:center;
                        font-family:Arial,sans-serif;'>
                        <h1 style='color:#343a40;'>%s</h1>
                        </div>
                        """, title);
    }

    private static String getFooter() {

        return """
                <div style='text-align:center;padding:20px; 
                background-color:#f8f9fa;
                font-family:Arial,sans-serif;'>
                 <p style='color:#6c757d;'>Thank you for choosing DIGI IMMO!</p>
                 <p style='color:#6c757d;'>The DIGI IMMO Team</p>
                 </div>
                <div style="text-align: center;">
                        <img src="https://res.cloudinary.com/dvp0fow8r/image/upload/v1737528662/utu1xwubwte52bbjproh.png"
                alt="Logo" style="max-width: 100px; height: auto; margin-bottom: 10px;">
                        </div>
                """;
    }

    public MultipartFile pdfgen(List<Bill> bills) throws IOException {

        if (bills.isEmpty()) return null;

        Users user = bills.getFirst().getUser();
        String billingMonth = bills.getFirst().getBillingMonth();
        LocalDate dueDate = bills.getFirst().getDueDate();

        double totalAmount = bills.stream().mapToDouble(Bill::getTotalAmount).sum();

        StringBuilder itemsHtml = new StringBuilder();
        for (Bill bill : bills) {
            itemsHtml.append("""
                        <tr>
                            <td>%s</td>
                            <td>%s</td>
                            <td>%s</td>
                            <td>$%.2f</td>
                            <td>%s</td>
                        </tr>
                    """.formatted(
                    bill.getType().equals(Bill.type.RESERVE_FUND) ? "RESERVE" : bill.getConsumption().getType().toString(),
                    bill.getType().toString(),
                    bill.getBillName(),
                    bill.getTotalAmount(),
                    bill.getBillId()
            ));
        }

        String htmlContent = String.format("""
                        <!DOCTYPE html>
                        <html>
                        <head>
                            <style>
                                body {
                                    font-family: Arial, sans-serif;
                                    padding: 20px;
                                    }
                                .invoice-box {
                                    border: 1px solid #eee;
                                    padding: 30px;
                                    max-width: 800px;
                                    margin: auto;
                                    box-shadow: 0 0 10px rgba(0, 0, 0, 0.15);
                                    }
                                h1, h2 {
                                    text-align: center;
                                    }
                                table {
                                    width: 100%%;
                                    border-collapse: collapse;
                                    margin-top: 20px;
                                    }
                                th, td {
                                    border: 1px solid #ddd;
                                    padding: 8px;
                                    text-align: left;
                                    }
                                .header-table, .details-table {
                                    width: 100%%;
                                    margin-bottom: 20px;
                                    }
                                .header-table td, .details-table td {
                                    border: none;
                                    padding: 5px;
                                    }
                                 .header-left {
                                    width: 50%%;
                                    vertical-align: top;
                                    }
                                 .header-right {
                                    width: 50%%;
                                    vertical-align: top;
                                    }
                                 .details-section {
                                    border: 1px solid #ddd;
                                    padding: 10px;
                                    margin-top: 10px;
                                    }
                                 .total-row {
                                    font-weight: bold;
                                    background-color: #f9f9f9;
                                    }
                            </style>
                        </head>
                        <body>
                        %s
                        <div class="invoice-box">
                            <table class="header-table">
                                <tr>
                                    <td class="header-left">
                                        <strong>Address of Apartment:</strong><br>
                                        %s, Floor %d, Flat %d (%s)<br>
                                        Box: %s
                                    </td>
                                    <td class="header-right">
                                        <strong>Syndic Details:</strong><br>
                                        Name: %s<br>
                                        Address: %s<br>
                                        BTW Number: %s
                                    </td>
                                </tr>
                            </table>
                            <table class="details-table">
                                <tr>
                                    <td>
                                        <strong>Billing Month:</strong> %s<br>
                                        <strong>Due Date:</strong> %s
                                    </td>
                                </tr>
                            </table>
                        
                            <div class="details-section">
                                <p><strong>User Type:</strong> %s</p>
                                <p><strong>Name:</strong> %s</p>
                                <p><strong>Email:</strong> %s</p>
                                <p><strong>Phone Number:</strong> %s</p>
                            </div>
                            <table>
                                <tr>
                                    <th>Charge Type</th>
                                    <th>Bill Type</th>
                                    <th>Bill Configuration</th>
                                    <th>User Consumption</th>
                                    <th>Bill Id</th>
                                </tr>
                                %s
                                <tr class="total-row">
                                    <td colspan="4">Total Amount</td>
                                    <td>$%.2f</td>
                                </tr>
                            </table>
                        </div>
                        %s
                        </body>
                        </html>
                        """,
                getHeader("Invoice"),
                user.getResidence(),
                user.getFloor(),
                user.getFlatNumber(),
                user.getFlatType(),
                user.getBoxNumber(),
                user.getBuilding().getSyndicName(),
                user.getBuilding().getSyndicAddress(),
                user.getBuilding().getSyndicBTW(),
                billingMonth,
                dueDate,
                user.getRole().name(),
                user.getFirstName() + " " + user.getLastName(),
                user.getEmail(),
                user.getPhoneNumber(),
                itemsHtml,
                totalAmount,
                getFooter()
        );
        File pdfFile = generatePdfFromHtml(htmlContent);
        //        String uploadedUrl = fileStackService.uploadFile(pdfMultipartFile);
        //        pdfFile.delete();

        return convertFileToMultipartFile(pdfFile);
    }


    private MultipartFile convertFileToMultipartFile(File pdfFile) throws IOException {
        return new MockMultipartFile(
                pdfFile.getName(),
                pdfFile.getName(),
                Files.probeContentType(pdfFile.toPath()),
                Files.readAllBytes(pdfFile.toPath())
        );
    }

    private File generatePdfFromHtml(String htmlContent) throws IOException {
        File pdfFile = createTempFile("receipt", ".pdf");

        try (OutputStream os = new FileOutputStream(pdfFile)) {
            ConverterProperties properties = new ConverterProperties();
            HtmlConverter.convertToPdf(htmlContent, os, properties); // Change here
        }
        return pdfFile;
    }
}
