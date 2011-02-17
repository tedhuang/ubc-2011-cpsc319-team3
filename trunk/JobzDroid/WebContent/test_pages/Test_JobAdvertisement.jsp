<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head> <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Testing Title</title>

<script type="text/javascript" src='../scripts/Utility.js'></script>
<script type="text/javascript" src='../scripts/JobAdvertisementCreation.js'></script>



<script type="text/javascript">

/***********************************************
* Drop Down Date select script- by JavaScriptKit.com
* This notice MUST stay intact for use
* Visit JavaScript Kit at http://www.javascriptkit.com/ for this script and more
***********************************************/

var monthtext=['Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sept','Oct','Nov','Dec'];

function populatedropdown(dayfield, monthfield, yearfield){
var today=new Date()
var dayfield=document.getElementById(dayfield)
var monthfield=document.getElementById(monthfield)
var yearfield=document.getElementById(yearfield)
for (var i=0; i<31; i++)
dayfield.options[i]=new Option(i, i+1)
dayfield.options[today.getDate()]=new Option(today.getDate(), today.getDate(), true, true) //select today's day
for (var m=0; m<12; m++)
monthfield.options[m]=new Option(monthtext[m], monthtext[m])
monthfield.options[today.getMonth()]=new Option(monthtext[today.getMonth()], monthtext[today.getMonth()], true, true) //select today's month
var thisyear=today.getFullYear()
for (var y=0; y<20; y++){
yearfield.options[y]=new Option(thisyear, thisyear)
thisyear+=1
}
yearfield.options[0]=new Option(today.getFullYear(), today.getFullYear(), true, true) //select today's year
}

</script>

 
 
<SCRIPT LANGUAGE="JavaScript">

// This script and many more are available free online at 
// The JavaScript Source!! http://javascript.internet.com 
// Created by: Lee Hinder, lee.hinder@ntlworld.com 

//set todays date
Now = new Date();
NowDay = Now.getDate();
NowMonth = Now.getMonth();
NowYear = Now.getYear();
if (NowYear < 2000) NowYear += 1900; //for Netscape

//function for returning how many days there are in a month including leap years
function DaysInMonth(WhichMonth, WhichYear)
{
  var DaysInMonth = 31;
  if (WhichMonth == "Apr" || WhichMonth == "Jun" || WhichMonth == "Sep" || WhichMonth == "Nov") DaysInMonth = 30;
  if (WhichMonth == "Feb" && (WhichYear/4) != Math.floor(WhichYear/4))	DaysInMonth = 28;
  if (WhichMonth == "Feb" && (WhichYear/4) == Math.floor(WhichYear/4))	DaysInMonth = 29;
  return DaysInMonth;
}

//function to change the available days in a months
function ChangeOptionDays(Which)
{
  DaysObject = eval("document.Form1." + Which + "Day");
  MonthObject = eval("document.Form1." + Which + "Month");
  YearObject = eval("document.Form1." + Which + "Year");

  Month = MonthObject[MonthObject.selectedIndex].text;
  Year = YearObject[YearObject.selectedIndex].text;

  DaysForThisSelection = DaysInMonth(Month, Year);
  CurrentDaysInSelection = DaysObject.length;
  if (CurrentDaysInSelection > DaysForThisSelection)
  {
    for (i=0; i<(CurrentDaysInSelection-DaysForThisSelection); i++)
    {
      DaysObject.options[DaysObject.options.length - 1] = null
    }
  }
  if (DaysForThisSelection > CurrentDaysInSelection)
  {
    for (i=0; i<(DaysForThisSelection-CurrentDaysInSelection); i++)
    {
      NewOption = new Option(DaysObject.options.length + 1);
      DaysObject.add(NewOption);
    }
  }
    if (DaysObject.selectedIndex < 0) DaysObject.selectedIndex == 0;
}

//function to set options to today
function SetToToday(Which)
{
  DaysObject = eval("document.Form1." + Which + "Day");
  MonthObject = eval("document.Form1." + Which + "Month");
  YearObject = eval("document.Form1." + Which + "Year");

  YearObject[0].selected = true;
  MonthObject[NowMonth].selected = true;

  ChangeOptionDays(Which);

  DaysObject[NowDay-1].selected = true;
}

//function to write option years plus x
function WriteYearOptions(YearsAhead)
{
  line = "";
  for (i=0; i<YearsAhead; i++)
  {
    line += "<OPTION>";
    line += NowYear + i;
  }
  return line;
}
//  End -->
</script>

</head>




<body onLoad="SetToToday('FirstSelect');">

<h1>Test Area</h1>

<FORM name="Form1">
<SELECT name="FirstSelectDay">
	<OPTION>1
	<OPTION>2
	<OPTION>3
	<OPTION>4
	<OPTION>5
	<OPTION>6
	<OPTION>7
	<OPTION>8
	<OPTION>9
	<OPTION>10
	<OPTION>11
	<OPTION>12
	<OPTION>13
	<OPTION>14
	<OPTION>15
	<OPTION>16
	<OPTION>17
	<OPTION>18
	<OPTION>19
	<OPTION>20
	<OPTION>21
	<OPTION>22
	<OPTION>23
	<OPTION>24
	<OPTION>25
	<OPTION>26
	<OPTION>27
	<OPTION>28
	<OPTION>29
	<OPTION>30
	<OPTION>31
</SELECT>
<SELECT name="FirstSelectMonth" onchange="ChangeOptionDays('FirstSelect')">
	<OPTION>Jan
	<OPTION>Feb
	<OPTION>Mar
	<OPTION>Apr
	<OPTION>May
	<OPTION>Jun
	<OPTION>Jul
	<OPTION>Aug
	<OPTION>Sep
	<OPTION>Oct
	<OPTION>Nov
	<OPTION>Dec
</SELECT>
<SELECT name="FirstSelectYear" onchange="ChangeOptionDays('FirstSelect')">
	<SCRIPT language="JavaScript">
		document.write(WriteYearOptions(50));
	</SCRIPT>
</SELECT>
</FORM>

==========================


<!-- Fields of Input -->
<br>

<h2>Create New Job Advertisement</h2>
Title: <input id="jobTitle" type="text" name="jobTitle" size="20"><br>

Description:  <br>
<textarea id="jobDescription" rows="4" cols="20">
</textarea> <br>

Education Requirement: 
<select id="educationRequirement" name="educationRequirement">
			<option value="0">None</option>
			<option value="1">B.Sc.</option>
			<option value="2">M.Sc.</option>
			<option value="3">Ph.D.</option>
		</select>
			
<br>
Job Location(s) (separated by comma): <input id="jobLocation" type="text" name="jobLocation" size="20"><br>
<br>

Contact Info: <input id="contactInfo" type="text" name="contactInfo" size="20"><br>
<br>

Tags: <input id="tags" type="text" name="tags" size="20"><br>


Starting Date:  
	<form action="" name="formStartingDate">
	<select id="startingDay">
	</select> 
	<select id="startingMonth">
	</select> 
	<select id="startingYear">
	</select> 
	</form>
	<script type="text/javascript">
		//populatedropdown(id_of_day_select, id_of_month_select, id_of_year_select)
		window.onload=function(){
		populatedropdown("startingDay", "startingMonth", "startingYear");
		};
	</script> 
	<br>

Expire In... : 
	<form action="" name="formExpiryDate">
	<select id="expiryDay">
	</select> 
	<select id="expiryMonth">
	</select> 
	<select id="expiryYear">
	</select> 
	</form>
	<script type="text/javascript">
		//populatedropdown(id_of_day_select, id_of_month_select, id_of_year_select)
		window.onload=function(){
		populatedropdown("expireDay", "expireMonth", "expireYear");
		};
	</script>
	<br>


<div id="feedback"><h3>Feedback Area</h3></div>
<button id="newJobAdButton" type="button" onclick="createJobAdvertisement()">Create Job Advertisement</button>

</body>
</html>


