import smtplib
from email.MIMEBase import MIMEBase
from email.MIMEText import MIMEText
from email.MIMEMultipart import MIMEMultipart
from email import Encoders
import os

def mail(subject, fromEmail, textMessage, file_name = ''):
    
# Create the container (outer) email message.
    msg = MIMEMultipart()
    msg['Subject'] = subject
    
    msg['From'] = fromEmail
    msg['To'] = fromEmail
    text = textMessage
    html = """
            <html>
            <head></head><body>%s</body></html>""" % (textMessage)
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
    print s
    s.sendmail(fromEmail, fromEmail, msg.as_string())
    s.close()
