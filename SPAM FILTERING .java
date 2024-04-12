USER REGISTRATION

public class UserRegistration extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		RegistrationFormregistrationForm=new RegistrationForm();
		
		String fname=request.getParameter("fname");
		String lname=request.getParameter("lname");
		
		String email=request.getParameter("email");
		String password=request.getParameter("password");
		
		String age=request.getParameter("age");
		String gender=request.getParameter("gender");
		
		String mobile=request.getParameter("mobile");
		String address=request.getParameter("address");

		registrationForm.setfName(fname);
		registrationForm.setAddress(address);
		registrationForm.setAge(Integer.parseInt(age));
		registrationForm.setEmail(email);
		registrationForm.setGender(gender);
		registrationForm.setlName(lname);
		registrationForm.setMobile(Long.parseLong(mobile));

		if(AppDAO.userRegistration(registrationForm,password)==2)
		{
			response.sendRedirect("signup.jsp?status=success");
		}
		else
		{
			response.sendRedirect("signup.jsp?status=User All Ready Exist");
		}
	}
}

REGISTRATION FORM


publicclassRegistrationForm {

	private String fName;
	private String lName;
	
	privateintage;
	private String gender;
	private String email;
	privatelongmobile;
	
	private String address;
	
	public String getfName() {
		returnfName;
	}

	publicvoidsetfName(String fName) {
		this.fName = fName;
	}

	public String getlName() {
		returnlName;
	}

	publicvoidsetlName(String lName) {
		this.lName = lName;
	}

	publicintgetAge() {
		returnage;
	}

	publicvoidsetAge(intage) {
		this.age = age;
	}

	public String getGender() {
		returngender;
	}

	publicvoidsetGender(String gender) {
		this.gender = gender;
	}

	public String getEmail() {
		returnemail;
	}

	publicvoidsetEmail(String email) {
		this.email = email;
	}

	publiclonggetMobile() {
		returnmobile;
	}

	publicvoidsetMobile(longmobile) {
		this.mobile = mobile;
	}

	public String getAddress() {
		returnaddress;
	}

	publicvoidsetAddress(String address) {
		this.address = address;
	}

}

FETCHING MAIL

publicclassFetchMails {

	privateArrayList<Mail>mails=newArrayList<Mail>();

	private Mail mail=null;
	privateArrayList<Attachment>attachments=null;

	publicArrayList<Mail> fetch(String pop3Host, String storeType, String user,Stringpassword)
	{
		try {

			// create properties field
			Properties properties = new Properties();
			properties.put("mail.store.protocol", "pop3");
			properties.put("mail.pop3.host", pop3Host);
			properties.put("mail.pop3.port", "995");
			properties.put("mail.pop3.starttls.enable", "true");
			Session emailSession = Session.getDefaultInstance(properties);

			// emailSession.setDebug(true);

			// create the POP3 store object and connect with the pop server
			Store store = emailSession.getStore("pop3s");

			store.connect(pop3Host, user, password);

			// create the folder object and open it
			Folder emailFolder = store.getFolder("INBOX");
			emailFolder.open(Folder.READ_ONLY);


			// retrieve the messages from the folder in an array and print it
			Message[] messages = emailFolder.getMessages();

			System.out.println("no of messages :\t"+messages.length);


			for (inti = 0; i<messages.length; i++) {

				mail=new Mail();
				mail.setId(AppDAO.getMaxMailBoxId());
				mail.setRead(false);

				attachments=newArrayList<Attachment>();

				Message message = messages[i];

				writePart(message);

				mail.setAttachments(attachments);

				mails.add(mail);

			}

			// close the store and folder objects
			emailFolder.close(false);
			store.close();

		} catch (NoSuchProviderExceptione) {
			e.printStackTrace();
		} catch (MessagingExceptione) {
			e.printStackTrace();
		} catch (IOExceptione) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		returnmails;
	}

	intattachmentIndex=0;

	publicvoidwritePart(Part p) throws Exception 
	{

		Address[] a;

		if (pinstanceof Message)
		{
			Message m=(Message)p;
			
			charc1='<';
			charc2='>';
			
			// FROM
			if ((a = m.getFrom()) != null) {
				for (intj = 0; j<a.length; j++)
					
					mail.setFrom(a[j].toString());
			}

			// TO
			if ((a = m.getRecipients(Message.RecipientType.TO)) != null) {
				for (intj = 0; j<a.length; j++)
				
				
					mail.setTo(a[j].toString());
			}

			// SUBJECT
			if (m.getSubject() != null)
				mail.setSubject(m.getSubject());
		}


		//check if the content has attachment
		if (p.isMimeType("multipart/*")) {
			Multipart mp = (Multipart) p.getContent();
			intcount = mp.getCount();
			for (inti = 0; i<count; i++)
				writePart(mp.getBodyPart(i));
		} 
		else {

			if (p.isMimeType(MimeTypes.TEXT_HTML)) {				
				mail.setHtmlBody((String)p.getContent());
			}
			else
			{
				if (p.isMimeType(MimeTypes.TEXT_PLAIN)) {				
					mail.setPlainBody((String)p.getContent());
				}
				else
				{
					Object obj=p.getContent();

					InputStreamis= (InputStream) obj;

					Attachment attachment=new Attachment();
					attachment.setIs(is);

					String fileName=""+mail.getId()+attachmentIndex;

					System.out.println("mime type is :\t"+p.getContentType());

					if (p.isMimeType(MimeTypes.IMAGE_GIF)) {				

						fileName=fileName+".gif";

						attachment.setMimeType(MimeTypes.IMAGE_GIF);
					}
					else
					{
						if (p.isMimeType(MimeTypes.IMAGE_JPEG)) {				
							fileName=fileName+".jpeg";
							attachment.setMimeType(MimeTypes.IMAGE_JPEG);
						}
						else
						{
							if (p.isMimeType(MimeTypes.IMAGE_PNG)) {				
								fileName=fileName+".png";
								attachment.setMimeType(MimeTypes.IMAGE_PNG);
							}
							else
							{
								if (p.isMimeType(MimeTypes.APPLICATION_EXCEL)) {				
									fileName=fileName+".xlsx";
									attachment.setMimeType(MimeTypes.APPLICATION_EXCEL);
								}
								else
								{
									if (p.isMimeType(MimeTypes.APPLICATION_MSWORD)) {				
										fileName=fileName+".docx";
										attachment.setMimeType(MimeTypes.APPLICATION_MSWORD);
									}
									else
									{
										if (p.isMimeType(MimeTypes.APPLICATION_PDF)) {				
											fileName=fileName+".pdf";
											attachment.setMimeType(MimeTypes.APPLICATION_PDF);
										}
									}//pdf

								}//word
							}//excel
						}//png
					}//jpg

					attachment.setAttachmentName(fileName);
					attachment.setMailId(mail.getId());
					attachments.add(attachment);
					attachmentIndex++;

				}//gif
			}

		} 
	}


LOGIN

public class LoginServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String email=request.getParameter("email").trim();
		String password=request.getParameter("password").trim();

		if(AppDAO.isValidUser(email,password)!=0)
		{
			request.getSession().setAttribute("email",email);
			request.getSession().setAttribute("password",password);

			RequestDispatcherrd=request.getRequestDispatcher("/RefreshServlet");
			rd.forward(request, response);
		}
		else
		{
			response.sendRedirect("index.jsp?status=Invalid Credentials");
		}
	}
}



publicclassRefreshServletextendsHttpServlet {
	privatestaticfinallongserialVersionUID = 1L;

	protectedvoiddoGet(HttpServletRequestrequest, HttpServletResponseresponse) throwsServletException, IOException {
		
		String host = "pop.gmail.com";// change accordingly
		String mailStoreType = "pop3";

		String username =(String)request.getSession().getAttribute("email");// change accordingly
		String password =(String)request.getSession().getAttribute("password");// change accordingly

		//Call method fetch

		ArrayList<Mail>mails=newFetchMails().fetch(host, mailStoreType, username, password);

		Iterator<Mail>it=mails.iterator();

		while(it.hasNext())
		{
			intcount=0;

			Mail mail=it.next();

			System.out.println(mail.getId());
			System.out.println(mail.getFrom());
			System.out.println(mail.getTo());

			System.out.println(mail.isRead());

			System.out.println("\n-------------Subject-------\n");
			System.out.println(mail.getSubject());

			System.out.println("\n-------------HTML Body-------\n");
			System.out.println(mail.getHtmlBody());

			System.out.println("\n-------------Text Body-------\n");

			String textBody=mail.getPlainBody();

			textBody=textBody+" "+mail.getSubject();

			WordNetDatabasedatabase=null;

			System.setProperty("wordnet.database.dir", "C:\\Program Files (x86)\\WordNet\\2.1\\dict\\");

			database= WordNetDatabase.getFileInstance();

			ArrayList<String>categorites=AppDAO.getCategoriesByUserID((String)request.getSession().getAttribute("email"));
			
			String[] cats=new String[categorites.size()];
			
			for(inti=0;i<categorites.size();i++)
			{
				cats[i]=categorites.get(i);
			}
			
			for(String cat : cats)
			{
				Synset[] synsets = database.getSynsets(cat,SynsetType.NOUN);

				Set<String>catWords=newTreeSet<String>();

				for (inti = 0; i<synsets.length; i++)
				{
					String[] wordForms = synsets[i].getWordForms();

					for (intj = 0; j<wordForms.length; j++)
					{
						String[] splits=wordForms[j].split(" ");

						for(intk=0;k<splits.length;k++)
						{
							catWords.add(splits[k]);
						}
					}//for

				}//for

				Iterator<String>wordit=catWords.iterator();

				System.out.println(textBody);

				while(wordit.hasNext())
				{	
					String synonym=wordit.next();

					System.out.println(synonym);

					if(textBody.contains(synonym)) {

						count++;
					}
				}
			}

			if(count>2)
			{
				mail.setStatus("spam");
			}
			else
			{
				mail.setStatus("inbox");
			}

			System.out.println(mail.getStatus());

			System.out.println("\n\n======================count is :\t"+count+"====================\n\n");

			ArrayList<Attachment>attachments=mail.getAttachments();

			Iterator<Attachment>it1=attachments.iterator();

			while(it1.hasNext())
			{
				Attachment attachment=it1.next();

				String mimeString=attachment.getMimeType();

				if(mimeString!=null)
				{
					InputStreamis=attachment.getIs();

					try {

						FileOutputStreamfos=new FileOutputStream(AppUtil.FILE_PATH+attachment.getAttachmentName());

						byte[] barr=newbyte[1024];

						intposition=0;

						while((position=is.read(barr))!=-1)
						{
							fos.write(barr, 0, position);
						}

						fos.close();
						is.close();

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}				
			}

			AppDAO.sendMail(mail);
			
		}
		
		response.sendRedirect("inbox.jsp?status=retrived");
		
	}

}
SETTINGS

publicclassSettingsServletextendsHttpServlet {
	
	protectedvoiddoPost(HttpServletRequestrequest, HttpServletResponseresponse) throwsServletException, IOException {
		
		String[] categories=request.getParameterValues("categories");
		
		String userId=(String)request.getSession().getAttribute("email");
		
		intresult=AppDAO.addCategories(categories, userId);
		
		if(result==1)
		{
			response.sendRedirect("settings.jsp?status=success");
		}
		else
		{
			response.sendRedirect("settings.jsp?status=failed");
		}
	}
}

ATTACHMENT

publicclass Attachment {
	
	privateintattachmentId;
	private String mimeType;
	privateInputStreamis;
	private String attachmentName;
	privateintmailId;
	
	public String getMimeType() {
		returnmimeType;
	}
	publicvoidsetMimeType(String mimeType) {
		this.mimeType = mimeType;
	}
	publicInputStreamgetIs() {
		returnis;
	}
	publicvoidsetIs(InputStreamis) {
		this.is = is;
	}
	publicintgetAttachmentId() {
		returnattachmentId;
	}
	publicvoidsetAttachmentId(intattachmentId) {
		this.attachmentId = attachmentId;
	}
	public String getAttachmentName() {
		returnattachmentName;
	}
	publicvoidsetAttachmentName(String attachmentName) {
		this.attachmentName = attachmentName;
	}
	publicintgetMailId() {
		returnmailId;
	}
	publicvoidsetMailId(intmailId) {
		this.mailId = mailId;
	}
}

MAIL FORM

publicclass Mail {
	
	privateintid;
	private String from;
	private String to;
	private String subject;
	private String htmlBody;
	private String plainBody;
	privateArrayList<Attachment>attachments=newArrayList<Attachment>();
	private String status;
	privatebooleanisRead;
	
	publicStringgetTo() {
		returnto;
	}
	publicvoidsetTo(String to) {
		this.to = to;
	}
	public String getSubject() {
		returnsubject;
	}
	publicvoidsetSubject(String subject) {
		this.subject = subject;
	}
	
	public String getFrom() {
		returnfrom;
	}
	publicvoidsetFrom(String from) {
		this.from = from;
	}
	publicintgetId() {
		returnid;
	}
	publicvoidsetId(intid) {
		this.id = id;
	}
	public String getStatus() {
		returnstatus;
	}
	publicvoidsetStatus(String status) {
		this.status = status;
	}
	publicbooleanisRead() {
		returnisRead;
	}
	publicvoidsetRead(booleanisRead) {
		this.isRead = isRead;
	}
	publicArrayList<Attachment>getAttachments() {
		returnattachments;
	}
	publicvoidsetAttachments(ArrayList<Attachment>attachments) {
		this.attachments = attachments;
	}
	public String getHtmlBody() {
		returnhtmlBody;
	}
	publicvoidsetHtmlBody(String htmlBody) {
		this.htmlBody = htmlBody;
	}
	public String getPlainBody() {
		returnplainBody;
	}
	publicvoidsetPlainBody(String plainBody) {
		this.plainBody = plainBody;
	}
}

