<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>FormDOM</title>
<link href="../../css/mainStyle.css" rel="stylesheet" type="text/css"/>
<link href="../../css/sideNavMenu.css" rel="stylesheet" type="text/css"/>
<link href="../../css/DynaSmartTab.css" rel="stylesheet" type="text/css"/>
</head>
<body>
<div class="main">
<div style="clear: both; height:2em; border-bottom: 6px ridge #79BAEC; margin: 30px 0 30px 0;"></div>

	<div id="edAdForm" class="edFormContainer">
		<h2 class="welcome">Editing Ad:</h2>
		   <div class="headToolBar">
					<ul id="edAdTool">
						<li><a class="jsBtn ed_saveDraft">Save Draft |</a></li>
						<li><a class="jsBtn ed_update" >Publish |</a></li>
						<li><a class="jsBtn ed_reset" > Reset Fields</a></li>
					</ul>
						<!-- <span class="feedback">Feed Back Area</span>-->
			  </div><!--ENDOF headBar-->
			  
		  	<div id="typeInForm" class="fillInForm"> 
				<div class="field">
					<label for="title-field" >Ad Title</label>
					<input id="title-filed" name="title-field" class="textBox mustNotNull" value="" />
					<input id="adId-field"  name="adId-field"  class="hidden mustNotNull" READONLY/>
				</div>
				<div class="field"> 
						<label for="contact-field" >Company:</label> 
						<input id="contact-filed" name="company-field" class="textBox mustNotNull" value="" /> 
					</div>
					<div class="field"> 
						<label for="tag-field" >Add Tags:</label> 
						<input id="tag-field" name="tag-field" class="textBox" value=""/> 
					</div>
			</div><!--ENDOF typeInForm-->
				
		<div id="chooseForm" class="fillInForm">
			<div class="field"> 
				<label for="edu-field">Minimal Degree Requirement:</label>
				<select id="edu-field" name="edu-field">
					<option value="">Not Specified</option>
					<option value="1">B.Sc.</option>
					<option value="2">M.Sc.</option>
					<option value="3">Ph.D.</option>
				</select>
			</div>
					
			<div class="field">
					<label for="startTime-field">Starting Date</label>
					<input id="startTime-field" name="startTime-field" class="mustNotNull" value="" READONLY/>
			</div >	
			<div class="field">
				<label for="expireTime-field">Expiry date</label>
				<input id="expireTime-field" name="expireTime-field" class="mustNotNull" value="" READONLY/>
				
			</div>
					
			<div id="jobAvailField" class="field"> 
				<label>Employment Type:</label>
				<label class="group">
					<input type="checkbox" name="ft-field" value="fullTime" id="ft-field" class="mustNotNull"/>
					Full Time
					<input type="checkbox" name="pt-field" value="partTime" id="pt-field" class="mustNotNull"/>
    				 Part Time
    				 <input type="checkbox" name="is-field" value="internship" id="is-field" class="mustNotNull"/>
   		 			Internship
				</label>
			</div>
			   </div><!--ENDOF RIGHT CHOOSEFORM-->
			   <div id="desc-div" class="descForm">
			       <div class="field"> 
						<label for="desc-field" >Job Description:</label> 
						<textarea id="desc-filed" name="desc-field" class="textBoxXL mustNotNull"></textarea> 
				   </div>
				</div>
		  </div><!--ENDOF edAdForm-->
		  
<div style="clear: both; height:2em; border-bottom: 6px ridge #79BAEC; margin: 30px 0 30px 0;"></div>
	
          
		  <div id="newAdForm" class="newFormContainer">
			  <div id="head-bar">
					<h2 class="welcome">Create New Ad</h2>
						<a class="jsBtn" onclick="postJobAd('draft', 'newAdForm','newAdfb')">Save to Draft |</a>
			    		<a class="jsBtn" onclick="postJobAd('submit','newAdForm','newAdfb')">Post it |</a>
						<a class="jsBtn" onclick="resetFields('newAdForm');"> Reset Fields</a>
						<span class="feedback">Feed Back Area</span>
			  </div><!--ENDOF headBar-->
		  
		  	<div id="typeInForm" class="fillInForm"> 
				<div class="field">
					<label for="title-field" >Ad Title:</label>
					<input id="title-filed" name="title-field" class="textBox mustNotNull" value="" />
				</div>
				<div class="field"> 
						<label for="contact-field" >Company:</label> 
						<input id="contact-filed" name="company-field" class="textBox mustNotNull" value="" /> 
					</div>
					
					<div class="field"> 
						<label for="tag-field" >Add Tags:</label> 
						<input id="tag-field" name="tag-field" class="textBox" value=""/> 
					</div>
				<div class="field"> 
					<label>Is Graduate Funding Available:</label>
						<input type="radio" name="gf-field" value="1" id="gf-field" class="mustNotNull"/> Yes
						<input type="radio" name="gf-field" value="0" id="gf-field" class="mustNotNull"/> No
				</div>
			</div><!--ENDOF typeInForm-->
				
		<div id="chooseForm" class="fillInForm">
			<div class="field"> 
				<label for="edu-field">Minimal Degree Requirement:</label>
				<select id="edu-field" name="edu-field">
					<option value="">Choose a degree</option>
					<option value="1">B.Sc.</option>
					<option value="2">M.Sc.</option>
					<option value="3">Ph.D.</option>
				</select>
			</div>
					
			<div class="field">
					<label for="startTime-field">Starting Date</label>
					<input id="startTime-field" name="startTime-field" class="textBox mustNotNull" value="" READONLY/>
			</div >	
			<div class="field">
				<label for="expireTime-field">Expiry date</label>
				<input id="expireTime-field" name="expireTime-field" class="textBox mustNotNull" value="" READONLY/>
			</div>
					
					<div class="field"> 
						<label>Available Positions:</label>
						<label class="group">
							<input type="checkbox" name="ft-field" value="fullTime" id="ft-field" class="mustNotNull"/>
							Full Time
							<input type="checkbox" name="pt-field" value="partTime" id="pt-field" class="mustNotNull"/>
		    				Part Time
		    				 <input type="checkbox" name="is-field" value="internship" id="is-field" class="mustNotNull"/>
		   		 			Internship
						</label>
					</div>
			   </div><!--ENDOF RIGHT CHOOSEFORM-->
			   <div id="fields">
			   	  
				  <div id="mapPanel">
				 	<div id="theMap">
				 		<label>Address: </label><input id="addrBar" class="map" type="text" />
					    <div id="map_canvas" ></div><br/>
					    <label>latitude: </label><input id="latitude" class="map"type="text"/><br/>
					    <label>longitude: </label><input id="longitude" class="map"type="text"/>
					    <button id="rmPtr">Clear Map</button>
				  	</div>
				  	<ul id="locList"></ul>
				 </div>
				</div><!-- ENDOF MAP -->
				<span class="jsBtn btn-slide">Add Location</span>
			   <div id="desc-div" class="fillInForm">
			       <div class="field"> 
						<label for="desc-field" >Job Description:</label> 
						<textarea id="desc-filed" name="desc-field" class="textBoxXL mustNotNull"></textarea> 
				   </div>
				</div>
		 </div><!--ENDOF newAdForm-->
<div style="clear: both; height:2em; border-bottom: 6px ridge #79BAEC; margin: 30px 0 30px 0;"></div>

		<div id="adDetailFrame" class="subFrame">
			<div class="headToolBar">
					<ul id="ownerDtlViewTool">
						<li><a class="jsBtn edit">Edit |</a></li>
						<li><a class="jsBtn del" >Delete </a></li>
					</ul>
			</div>
			 <div id="adDetailTable" class="resultTableDiv noBorder">
			 	<span class="feedback"></span>
			 	<h2 id="adDetailHeading" class="welcome heading"></h2>
				<table>
					<tbody>
					</tbody>
				</table>
			 </div>		
		</div><!--end of TABDETAILFRAME-->   
<div style="clear: both; height:2em; border-bottom: 6px ridge #79BAEC; margin: 30px 0 30px 0;"></div>

<div id="searchSearcherFrame" class="subFrame">
 <div id="advSearchForm" class="content">
		       	<label class="heading-text">Search Job Searchers</label><br/><br/>
			   
			     <div class="columnBox">
			     	<div class="formFieldWrapper">
						<label class="formFieldLabel">Job Searcher's Name: </label><br/>
						<input id="nameSearch" type="text" name="searchJSName" size="40" />
					</div>
					<div class="formFieldWrapper">
						<label class="formFieldLabel">Location:</label><br />
						<input id="jsLocSearch" type="text" name="searchJSLoc" size="40" />
					</div>
				 </div>	<!--ENDOF columnBox-->
				 
			 <div class="columnBox">
			 	<div class="formFieldWrapper">
					<label class="formFieldLabel">Minimum Educational Requirement: </label><br />
					<select id="jsEduReqSearch" name="searchJSEduReq">
							<option value="">Choose a degree</option>
							<option value="1">B.Sc.</option>
							<option value="2">M.Sc.</option>
							<option value="3">Ph.D.</option>
					</select>
				</div>
				<div class="formFieldWrapper">
					<label class="formFieldLabel">Can Work Starting:</label><br/>
					<input id="jsStartTime-field" name="searchJSStartTime" type="text" size="40" />
				</div>	
			  </div><!--ENDOF columnBox-->
			  <br/>
			  <div class="floatLeftBtn"> 
				<button id="newJSSearchBtn" type="button" onclick="searchSearcherProfile('resultTable')">Search</button>
				<div id="feedback"><h2>Let's Do Some Search!</h2></div>
			  </div><!--  ENDOF floatLeftBtn -->			  

	
			</div><!--END ADVSEARCHFORM-->
			
			<div id="resultTable" class="resultTableDiv">
				<table>
					<thead>
					<tr>
						<th id="col-jsName">
							<div id="colBtn" class="columnButton" onclick=""><!--Prepare to click sorting-->
								<div class="headText">Name</div>
							</div>
						</th>
						<th id="col-jsEduReq">
							<div id="colBtn" class="columnButton" onclick=""><!--Prepare to click sorting-->
								<div class="headText">Degree</div>
							</div>
						</th>
						<th id="col-jsLocation">
							<div id="colBtn" class="columnButton" onclick=""><!--Prepare to click sorting-->
								<div class="headText">Location</div>
							</div>
						</th>
						<th id="col-jsStartDate">
							<div id="colBtn" class="columnButton" onclick=""><!--Prepare to click sorting-->
								<div class="headText">Starting Date</div>
							</div>
						</th>
					</tr>
				</thead>
					<tbody>
					</tbody>
				</table><!--ENDOF TABLE-->	
			  </div><!--ENDOF DIV RESULT TABLE-->
	</div><!-- ENDOF SEARCH-SEARCHER FRAME -->

<div style="clear: both; height:2em; border-bottom: 6px ridge #79BAEC; margin: 30px 0 30px 0;"></div>

<div id="jsDetailFrame" class="subFrame">
		<div id="jsDetailTable" class="resultTableDiv noBorder">
		 	<span id="detailFB"></span>
		 	<h2 class="jsDetailHeading" class="welcome"></h2>
			<table>
				<tbody>
				</tbody>
			</table>
		 </div>		
		 <div id="fileDiv" class="resultTableDiv">
			<table>
				<thead>
				<tr>
					<th id="col-fileName" style="text-align:center">
						File Name
					</th>
					<th id="col-fileSize" style="text-align:center">
						Size (in MB)
					</th>
				</tr>
			</thead>
				<tbody>
				</tbody>
			</table><!--ENDOF TABLE-->
		</div><!--ENDOF File TABLE--> 
	</div><!--end of js-DETAILFRAME--> 
	
</div><!-- ENDOF MAIN -->		  
</body>
</html>