import smtplib
from email.MIMEBase import MIMEBase
from email.MIMEText import MIMEText
from email.MIMEMultipart import MIMEMultipart
from email import Encoders
import os

def mail(subject, fromEmail, textMessage, file_name = ''):
    
# Create the container (outer) email message.
    msg = MIMEMultipart()
    msg['Subject'] = "Lunch Treat On Wednesday:-)"
    
    msg['From'] = "ShubhadaAshok.Bongarde@synchronoss"
    msg['To'] = "Kiran.PS2@synchronoss.com,Sowmya.S@synchronoss.com,Jhansi.Nayak@synchronoss.com,Ajith.Kumar@synchronoss.com,AvinashLokaVenkata.Bayana@synchronoss.com,Chethan.Rao@synchronoss.com,Siddesh.Rumale@synchronoss.com,Uma.SankarGuda@synchronoss.com,Sagar.Rath@synchronoss.com"
    #msg['To'] = "Kiran.PS2@synchronoss.com"
    msg['Cc'] = "ShubhadaAshok.Bongarde@synchronoss"
    text = textMessage
    html = """
            <html>
            <head></head><body>Hi All,<br> <br> 
I would like to take you guys for a Lunch( wedding anniversary treat ) on this Wednesday so please don't bring boxe's. 
<br><br>@Chethan : Could you please postpone your lunch treat to Thursday. 
<br><br> 
Thanks<br>
Shubhada</body></html>"""
    body = MIMEMultipart('alternative')
    part1 = MIMEText(text, 'plain')
    part2 = MIMEText(html, 'html')
    body.attach(part1)
    body.attach(part2)
    msg.attach(body)

    if file_name != '':
        attachFile = MIMEBase('application', 'octet-stream')
        attachFile.set_payload(open(file_name,'rb').read())
        Encoders.encode_base64(attachFile)
        attachFile.add_header('Content-Disposition', 'attachment; filename="%s";' % os.path.basename(file_name))
        msg.attach(attachFile)
        
    s = smtplib.SMTP()
    s.connect("outgoing.sncrcorp.net","25")
    print s.sendmail(fromEmail, "Kiran.PS2@synchronoss.com", msg.as_string())
    print s
    s.close()
mail('ShubhadaAshok.Bongarde@synchronoss','ShubhadaAshok.Bongarde@synchronoss',"""Hi All, 
I would like to take you guys for a Lunch on this Wednesday so please don't bring boxe's. 
@Chethan : Could you please postpone your lunch treat to Thursday. 

Thanks
Shubhada A """)
