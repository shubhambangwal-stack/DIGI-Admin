package com.vunum.SocietyAdmin.Utilities;

import com.vunum.SocietyAdmin.entity.Bill;
import com.vunum.SocietyAdmin.entity.Helpdesk;
import com.vunum.SocietyAdmin.entity.Notice;
import com.vunum.SocietyAdmin.entity.Users;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MailTemplates {

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

    public static String noticeBuilding(Notice notice) {
        return """
                 <!DOCTYPE html>
                 <html>
                 <head>
                     <style>
                         body { font-family: Arial, sans-serif; padding: 20px; }
                         .notice-box {
                             border: 1px solid #eee;
                             padding: 30px;
                             max-width: 700px;
                             margin: auto;
                             box-shadow: 0 0 10px rgba(0, 0, 0, 0.15);
                         }
                         h2 { text-align: center; color: #4CAF50; }
                         p { font-size: 16px; line-height: 1.6; }
                     </style>
                 </head>
                 <body>
                     %s
                     <div class="notice-box">
                         <h2>Building Notice: %s</h2>
                         <p><strong>Issued On:</strong> %s</p>
                         <p>%s</p>
                     </div>
                     %s
                 </body>
                 </html>
                """.formatted(
                getHeader("A Notice has been assigned to your building!"),
                notice.getTitle(),
                notice.getIssuedOn().toLocalDate().toString(),
                notice.getDescription(),
                getFooter()
        );
    }

    public static String noticeSpecific(Notice notice) {
        return """
                 <!DOCTYPE html>
                 <html>
                 <head>
                     <style>
                         body { font-family: Arial, sans-serif; padding: 20px; }
                         .notice-box {
                             border: 1px solid #eee;
                             padding: 30px;
                             max-width: 700px;
                             margin: auto;
                             box-shadow: 0 0 10px rgba(0, 0, 0, 0.15);
                         }
                         h2 { text-align: center; color: #2196F3; }
                         p { font-size: 16px; line-height: 1.6; }
                     </style>
                 </head>
                 <body>
                     %s
                     <div class="notice-box">
                         <h2>Personal Notice: %s</h2>
                         <p><strong>Issued On:</strong> %s</p>
                         <p><strong>To:</strong> %s %s</p>
                         <p>%s</p>
                     </div>
                     %s
                 </body>
                 </html>
                """.formatted(
                getHeader("A Notice has been assigned to you"),
                notice.getTitle(),
                notice.getIssuedOn().toLocalDate().toString(),
                notice.getUser() != null ? notice.getUser().getFirstName() : "",
                notice.getUser() != null ? notice.getUser().getLastName() : "",
                notice.getDescription(),
                getFooter()
        );
    }


    public static String accountAddedEmail(String userName) {

        return getHeader("Account Added Successfully!") +
                "<div style='padding:20px;font-family:Arial,sans-serif;'>"
                + "<p>Dear <strong>" + userName + "</strong>,</p>"
                + "<p>You have successfully added a new email to your profile. " +
                "Enjoy the enhanced experience!</p>"
                + "<p style='color:#17a2b8;'>If you have any questions, " +
                "feel free to reach out to our support team.</p>"
                + "</div>" + getFooter();
    }

    public static String approvalMail(Users user) {
        return """
                     <!DOCTYPE html>
                     <html>
                     <head>
                         <style>
                             body { font-family: Arial, sans-serif; padding: 20px; }
                             .invoice-box {
                                 border: 1px solid #eee;
                                 padding: 30px;
                                 max-width: 700px;
                                 margin: auto;
                                 box-shadow: 0 0 10px rgba(0, 0, 0, 0.15);
                             }
                             h2 { text-align: center; color: #4CAF50; }
                             p { font-size: 16px; line-height: 1.6; }
                             table { width: 100%%; border-collapse: collapse; margin-top: 20px; }
                             th, td { padding: 10px; border: 1px solid #ddd; text-align: left; }
                             .total-row td { font-weight: bold; background-color: #f9f9f9; }
                             .btn {
                                 display: inline-block;
                                 padding: 10px 20px;
                                 font-size: 16px;
                                 color: white;
                                 background-color: #4CAF50;
                                 text-decoration: none;
                                 border-radius: 5px;
                                 margin-top: 20px;
                             }
                         </style>
                     </head>
                     <body>
                         %s
                         <div class="invoice-box">
                             <h2>Approval Notification</h2>
                             <p>Dear %s,</p>
                             <p>We are pleased to inform you that your account has been approved! You can now enjoy full access to our services.</p>
                             <p>If you have any questions or need assistance, please do not hesitate to contact our support team.</p>
                             <p>Thank you for being a part of our community!</p>
                         </div>
                         %s
                     </body>
                     </html>
                """.formatted(getHeader("Congratulations!"), user.getFirstName(), getFooter());
    }

    public static String ticket(Helpdesk ticket, String status) {
        return """
                 <!DOCTYPE html>
                 <html>
                 <head>
                     <style>
                         body { font-family: Arial, sans-serif; padding: 20px; }
                         .ticket-box {
                             border: 1px solid #eee;
                             padding: 30px;
                             max-width: 700px;
                             margin: auto;
                             box-shadow: 0 0 10px rgba(0, 0, 0, 0.15);
                         }
                         h2 { text-align: center; color: #FF9800; }
                         p { font-size: 16px; line-height: 1.6; }
                         .images { margin-top: 20px; }
                         .images img { max-width: 100px; margin-right: 10px; }
                     </style>
                 </head>
                 <body>
                     %s
                     <div class="ticket-box">
                         <h2>Helpdesk Ticket: %s</h2>
                         <p><strong>Subject:</strong> %s</p>
                         <p><strong>Description:</strong> %s</p>
                         <p><strong>Status:</strong> %s</p>
                         <p><strong>Created At:</strong> %s</p>
                         %s
                     </div>
                     %s
                 </body>
                 </html>
                """.formatted(
                getHeader("Helpdesk Ticket " + status + "!"),
                ticket.getTicketID(),
                ticket.getTicketSubject(),
                ticket.getTicketDescription(),
                ticket.getTicketStatus().name(),
                ticket.getCreatedAt().toLocalDate().toString(),
                ticket.getImageUrl() != null && !ticket.getImageUrl().isEmpty() ?
                        "<div class='images'>" +
                                ticket.getImageUrl().stream()
                                        .map(url -> "<img src='" + url + "' alt='Ticket Image' />")
                                        .collect(Collectors.joining()) +
                                "</div>" : "",
                getFooter()
        );
    }


    public String generateInvoiceHtml(List<Bill> bills) {
        if (bills.isEmpty()) return "";

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

        return """
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
                         .centered-table {
                            width: 100%%;
                            border-collapse: collapse;
                            margin: 20px auto 0;
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
                    <table class="centered-table">
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
                """.formatted(
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
                itemsHtml.toString(),
                totalAmount,
                getFooter()
        );
    }


}
