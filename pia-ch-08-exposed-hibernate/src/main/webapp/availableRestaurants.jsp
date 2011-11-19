<?xml version='1.0' encoding='utf-8'?><%@ page contentType="text/xml" autoFlush="false" import="net.chrisrichardson.foodToGo.views.*,java.util.*" %>

<%
PendingOrderDetail pendingOrder = (PendingOrderDetail)request.getAttribute("pendingOrder");
Collection restaurants = (Collection)request.getAttribute("restaurants");
%>

<available-restaurants>

<delivery-address><%= pendingOrder.getDeliveryAddress().getStreet1() %></delivery-address>

<restaurants>

<%
for (Iterator it = restaurants.iterator(); it.hasNext(); ) {
		RestaurantDetail r = (RestaurantDetail)it.next();
%>
<restaurant><%= r.getName() %></restaurant>

<%
}
%>
</restaurants>

</available-restaurants>