<%@ page language="java" %>

<h1>PromptForDeliveryAddressAndTime.jsp</h1>

<form action="updateDeliveryInfo" method="post">

<p>Street1: <input type="text" name="street1"/> 
<p>Street2:  <input type="text" name="street2"/>
<p>City:  <input type="text" name="city"/>
<p>State:  <input type="text" name="state"/>
<p>Zip:  <input type="text" name="zip"/>

<p>Date: <select name="day">
<option value="today">Today</option>
<option value="tomorrow">Tomorrow</option>
</select>

<p>Time: <select name="hour">
<option value="noon">noon</option>
<option value="1pm">1pm</option>
</select> : <select property="minute">
<option value="noon">0</option>
<option value="1pm">15</option>
<option value="1pm">30</option>
<option value="1pm">45</option>
</select>


<p>
<input type="submit" value="next"/>

</form>

