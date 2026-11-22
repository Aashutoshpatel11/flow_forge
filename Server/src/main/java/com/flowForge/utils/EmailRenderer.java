package com.flowForge.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailRenderer {

    @Value("client.dashboard.url")
    private final static String clientURL = null;

    public String renderWrapper(String subject, String body) {

        return """
                <!DOCTYPE html>
                <html lang="en" xmlns="http://www.w3.org/1999/xhtml" xmlns:v="urn:schemas-microsoft-com:vml" xmlns:o="urn:schemas-microsoft-com:office:office">
                <head>
                  <meta charset="utf-8">
                  <meta name="viewport" content="width=device-width, initial-scale=1.0">
                  <meta http-equiv="X-UA-Compatible" content="IE=edge">
                  <meta name="x-apple-disable-message-reformatting">
                  <title>%s</title>
                  <!--[if mso]>
                  <noscript>
                    <xml>
                      <o:OfficeDocumentSettings>
                        <o:PixelsPerInch>96</o:PixelsPerInch>
                      </o:OfficeDocumentSettings>
                    </xml>
                  </noscript>
                  <![endif]-->
                  <style>
                    body, table, td, a { -webkit-text-size-adjust: 100%%; -ms-text-size-adjust: 100%%; }
                    table, td { mso-table-lspace: 0pt; mso-table-rspace: 0pt; }
                    img { -ms-interpolation-mode: bicubic; border: 0; height: auto; line-height: 100%%; outline: none; text-decoration: none; }
                    body { margin: 0 !important; padding: 0 !important; width: 100%% !important; background-color: #f4f6f9; }
                    @media screen and (max-width: 600px) {
                      .email-container { width: 100%% !important; max-width: 100%% !important; }
                      .mobile-padding { padding-left: 24px !important; padding-right: 24px !important; }
                    }
                  </style>
                </head>
                <body style="margin: 0; padding: 0; background-color: #f4f6f9; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif; color: #1e293b;">

                  <table role="presentation" cellspacing="0" cellpadding="0" border="0" width="100%%" style="background-color: #f4f6f9;">
                    <tr>
                      <td align="center" style="padding: 40px 16px;">

                        <table role="presentation" cellspacing="0" cellpadding="0" border="0" width="600" class="email-container" style="max-width: 600px; width: 100%%; background-color: #ffffff; border-radius: 12px; overflow: hidden; box-shadow: 0 4px 20px rgba(15, 23, 42, 0.06); border: 1px solid #e2e8f0;">

                          <tr>
                            <td style="height: 5px; background: linear-gradient(90deg, #4f46e5 0%%, #06b6d4 100%%); background-color: #4f46e5; font-size: 0; line-height: 0;">&nbsp;</td>
                          </tr>

                          <tr>
                            <td class="mobile-padding" style="padding: 28px 40px 20px 40px; border-bottom: 1px solid #f1f5f9;">
                              <a href="https://flowforge.example.com" target="_blank" style="text-decoration: none; display: inline-block;">
                                <span style="display: inline-block; width: 28px; height: 28px; line-height: 28px; text-align: center; background-color: #4f46e5; color: #ffffff; font-weight: 700; font-size: 14px; border-radius: 6px; vertical-align: middle; margin-right: 8px;">FF</span>
                                <span style="font-size: 18px; font-weight: 700; color: #0f172a; letter-spacing: -0.02em; vertical-align: middle;">Flow Forge</span>
                              </a>
                            </td>
                          </tr>

                          <tr>
                            <td class="mobile-padding" style="padding: 32px 40px 12px 40px;">
                              <h1 style="margin: 0; font-size: 24px; line-height: 1.3; font-weight: 700; color: #0f172a; letter-spacing: -0.02em;">
                                %s
                              </h1>
                            </td>
                          </tr>

                          <tr>
                            <td class="mobile-padding" style="padding: 12px 40px 36px 40px; font-size: 15px; line-height: 1.65; color: #334155;">
                              %s
                            </td>
                          </tr>

                          <tr>
                            <td class="mobile-padding" style="padding: 24px 40px; background-color: #f8fafc; border-top: 1px solid #e2e8f0; font-size: 12px; line-height: 1.6; color: #64748b;">
                              <p style="margin: 0 0 8px 0; font-weight: 600; color: #475569;">Flow Forge</p>
                              <p style="margin: 0 0 12px 0;">
                                Automate workflows, connect tools, and ship faster.
                              </p>
                              <p style="margin: 0;">
                                <a href="%s" style="color: #4f46e5; text-decoration: none;">Dashboard</a>
                              </p>
                            </td>
                          </tr>

                        </table>

                      </td>
                    </tr>
                  </table>

                </body>
                </html>
                """.formatted(
                subject,
                subject,
                body,
                clientURL
        );
    }
}