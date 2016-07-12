<dsp:page>
	<dsp:importbean bean="/custom/Cat" />
	<head>
<title>update-cat</title>
	</head>
	<body>
		<dsp:droplet name="/droplet/CatLookup">
			<dsp:param name="catId" param="id" />
			<dsp:setvalue param="cat" paramvalue="element" />
			<dsp:oparam name="output">
				<dsp:getvalueof var="catName" param="cat.catName" />
				<dsp:getvalueof var="breed" param="cat.breed" />
				<dsp:getvalueof var="pedigree" param="cat.pedigreeNumber" />
				<dsp:getvalueof var="showList" param="cat.showList" />
				<dsp:getvalueof var="vaccinations" param="cat.vaccinations" />
				<dsp:getvalueof var="id" param="cat.catId" />
			</dsp:oparam>
		</dsp:droplet>
		<p>${catName}
		<p>${pedigree}
		<p>

			<dsp:droplet name="/atg/dynamo/droplet/ForEach">
				<dsp:param name="array" value="${showList}" />
				<dsp:setvalue param="show" paramvalue="element" />

				<dsp:oparam name="empty">
					There are no items.<br />
				</dsp:oparam>

				<dsp:oparam name="outputStart">
					show list: <br />
				</dsp:oparam>

				<dsp:oparam name="output">

					<li>Show # <dsp:valueof param="count" /> :
					<dsp:valueof param="show.showName" /><br>
					</li>

				</dsp:oparam>

				<dsp:oparam name="outputEnd">
					Shows number is : <dsp:valueof param="size" />
					<br />
				</dsp:oparam>

			</dsp:droplet>
	</body>
</dsp:page>