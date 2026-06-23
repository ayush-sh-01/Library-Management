import os
from reportlab.lib.pagesizes import letter
from reportlab.platypus import SimpleDocTemplate, Paragraph, Spacer, Table, TableStyle
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
from reportlab.lib import colors

def create_pdf(filename="LMS_Workflow_Guide.pdf"):
    doc = SimpleDocTemplate(filename, pagesize=letter,
                            rightMargin=54, leftMargin=54, topMargin=54, bottomMargin=54)
    story = []
    styles = getSampleStyleSheet()
    
    # Custom Hinglish styles
    title_style = ParagraphStyle(
        'TitleStyle',
        parent=styles['Heading1'],
        fontSize=22,
        leading=26,
        textColor=colors.HexColor('#1A365D'),
        spaceAfter=15
    )
    
    subtitle_style = ParagraphStyle(
        'SubtitleStyle',
        parent=styles['Heading2'],
        fontSize=13,
        leading=16,
        textColor=colors.HexColor('#2B6CB0'),
        spaceAfter=8,
        spaceBefore=12
    )
    
    body_style = ParagraphStyle(
        'BodyStyle',
        parent=styles['Normal'],
        fontSize=10,
        leading=14,
        textColor=colors.HexColor('#2D3748'),
        spaceAfter=8
    )
    
    bullet_style = ParagraphStyle(
        'BulletStyle',
        parent=body_style,
        leftIndent=20,
        firstLineIndent=-10,
        spaceAfter=4
    )

    story.append(Paragraph("Library Management System (LMS) - Guide", title_style))
    story.append(Paragraph("Hinglish Version & Code Workflow Explanation", subtitle_style))
    story.append(Spacer(1, 10))
    
    # Yes or No section
    story.append(Paragraph("<b>Is this project completed and fully working?</b>", subtitle_style))
    story.append(Paragraph("<b>YES!</b> Project bilkul complete hai, fully functional hai aur production-ready design ke sath built hai. Aap matches lookup, checks, fines aur records management standard Core Java parameters ke through control kar sakte hain.", body_style))
    
    # Eclipse IDE Section
    story.append(Paragraph("<b>Kya hum isse Eclipse IDE me use kar sakte hain?</b>", subtitle_style))
    story.append(Paragraph("<b>YES!</b> Aap isse Eclipse me bina kisi configuration issue ke direct import kar sakte hain:", body_style))
    story.append(Paragraph("1. Eclipse open karein aur <i>File -> Import -> General -> Existing Projects into Workspace</i> select karein.", bullet_style))
    story.append(Paragraph("2. Select root directory me hamara project <code>library_management_system</code> select karein aur Finish click karein.", bullet_style))
    story.append(Paragraph("3. Agar direct import me Eclipse configuration issue deta hai, toh select <i>File -> New -> Java Project</i> banayein, aur project name <code>LibraryManagementSystem</code> rakh kar humare <code>src/</code> folder ke packages ko copy-paste kar dein.", bullet_style))
    story.append(Paragraph("4. `com.library.Main.java` par right-click karke <b>Run As -> Java Application</b> select karein. Console window me menus pop up ho jayenge.", bullet_style))
    
    # Package Structure
    story.append(Paragraph("<b>Code Structure (Package wise explanation)</b>", subtitle_style))
    story.append(Paragraph("• <b>com.library.model:</b> Book, LibraryMember (Abstract), Student, Faculty, aur Transaction details store karta hai. Dynamic rules follow karne ke liye Student aur Faculty, LibraryMember ke children hain.", bullet_style))
    story.append(Paragraph("• <b>com.library.io:</b> Isme <code>FileHandler.java</code> data parsing handle karta hai. CSV records readable files ki tarah save hotey hain.", bullet_style))
    story.append(Paragraph("• <b>com.library.service:</b> Business operations (issue, return, delete validation, top charts) control karta hai.", bullet_style))
    story.append(Paragraph("• <b>com.library.util:</b> DataSeeder (random 1,000+ items create karne ke liye) aur VerificationTest scripts isme hain.", bullet_style))
    
    # Workflow
    story.append(Paragraph("<b>Workflow Kaise Kaam Karta Hai?</b>", subtitle_style))
    story.append(Paragraph("1. <b>Startup Setup:</b> Main class initialize hone par `data/` folder read karti hai. Agar user ke system pe data files missing hain toh auto-seeder prompt load hota hai.", bullet_style))
    story.append(Paragraph("2. <b>Issue Book:</b> Book ID and Member ID feed hone par service class availability double-check karti hai, tab use checkout flag lagakar transactions history file me save karti hai.", bullet_style))
    story.append(Paragraph("3. <b>Return Book & Fines:</b> System target check-in and checkout due date ke absolute days difference calculates karta hai. Polymorphism runtime dynamically decide karta hai ki agar student late hai toh $2.00 rate (capped at $50) and faculty late hai toh $1.00 rate (capped at $20) fine apply hoga.", bullet_style))

    # Save
    doc.build(story)
    print("PDF created successfully!")

if __name__ == '__main__':
    create_pdf()
