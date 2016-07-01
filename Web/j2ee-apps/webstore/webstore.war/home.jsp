<dsp:page>
	<dsp:importbean bean="/custom/Cat" />
	<head>
<title>Home</title>
	</head>
	<body>
		<dsp:droplet name="/droplet/PrintAllCats">
			<dsp:oparam name="startOutput">
				Were found <dsp:valueof param="numItems" /> books:
			</dsp:oparam>
			<dsp:oparam name="output">
				<li><dsp:a href="">
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