<dsp:page>
	<dsp:importbean bean="/custom/Cat" />
	<head>
<title>Home</title>
	</head>
	<body>
		<dsp:droplet name="/droplet/PrintAllCats">
			<dsp:oparam name="startOutput">
				There are <dsp:valueof param="numItems" /> cats:
			</dsp:oparam>
			<dsp:oparam name="output">
				<li><dsp:a href="/training/update-cat.jsp">
						<dsp:param name="id" param="item.catId" />
						<dsp:valueof param="item.catName" />
					</dsp:a>
			</dsp:oparam>
			<dsp:oparam name="empty">
				<dsp:valueof param="emptyMessage" />
			</dsp:oparam>
			<dsp:oparam name="error">
				<dsp:valueof param="errorMessage" />
			</dsp:oparam>
		</dsp:droplet>
	</body>
</dsp:page>