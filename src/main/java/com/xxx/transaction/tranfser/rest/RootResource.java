package com.xxx.transaction.tranfser.rest;

import com.xxx.transaction.tranfser.ITransfer;
import com.xxx.transaction.tranfser.TradingSystem;
import com.xxx.transaction.tranfser.TransferFactory;
import com.xxx.transaction.tranfser.TransferRuntimeException;
import com.xxx.transaction.tranfser.util.OptionParser;
import com.xxx.transaction.tranfser.util.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.*;
import javax.ws.rs.core.Response.ResponseBuilder;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

@Path("/")
public class RootResource {
    private static final Log LOG = LogFactory.getLog(RootResource.class);

    public static final String CLICHED_MESSAGE = "Hello World!";
    public static final String OK_MESSAGE = "OK";
	
	public RootResource(){}

    @POST
    @Path("/user/add")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response add(final @Context UriInfo uriInfo, String param) {
        ITransfer transfer = TransferFactory.getTransfer();
        LOG.info("Request on url " + uriInfo.getAbsolutePath());

        JSONObject result = new JSONObject();

        List<String[]> pairs = null;
        try {
            pairs = OptionParser.convertToPairCoin(param);
        } catch(JSONException je) {
            LOG.warn("Illegal Arguments " + param + ", " + StringUtils.stringifyException(je));
            result.put(uriInfo.getPath(), "IllegalArgumentsException");
            return Response.ok(result.toString()).build();
        }

        JSONObject status = new JSONObject();
        for (String[] tuple : pairs) {
            try {
                transfer.addUserCoin(tuple[0], Integer.parseInt(tuple[1]));
                status.put(tuple[0], OK_MESSAGE);
            } catch(TransferRuntimeException te) {
                status.put(tuple[0], te.getMessage());
            }
        }

        result.append(uriInfo.getPath(), status);
        ResponseBuilder response = Response.ok(result.toString());

        return response.build();
    }

    @POST
	@Path("/transaction/transfer")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.TEXT_PLAIN)
	public Response transfer(final @Context UriInfo uriInfo, String param) {
        ITransfer transfer = TransferFactory.getTransfer();
        LOG.info("Request on url " + uriInfo.getAbsolutePath());

        Transaction transaction = new Transaction();
        try {
            OptionParser.convertToTransaction(param, transaction);
        } catch(RuntimeException re) {
            LOG.warn("Invalid parameters for " + param + ", " + StringUtils.stringifyException(re));
            return Response.ok("Invalid parameters for " + param).build();
        }

        try {
            transfer.transferTo(transaction.from_user, transaction.to_user, transaction.coins);
        } catch(TransferRuntimeException te) {
            LOG.warn("Failed to transfer the coins from " + transaction.from_user +
                    ", to " + transaction.to_user + ", " + StringUtils.stringifyException(te));
            return Response.ok(te.getMessage()).build();
        }

		return Response.ok("OK").build();
	}
	
	
	@GET
    @Produces("text/plain")
    public String getHello() {
        return CLICHED_MESSAGE;
    }

    @GET
    @Path("/jstack")
    @Produces("text/plain")
    public String getThreadDump() {
        StringWriter stm = new StringWriter();
        PrintWriter wrt = new PrintWriter(stm);

        TradingSystem.printThreadInfo(wrt, "jstack");
        wrt.close();
        return stm.toString();
    }

    public class Transaction {
        String from_user;
        String to_user;
        int coins;

        public void setFrom_user(String from_user) {
            this.from_user = from_user;
        }

        public void setTo_user(String to_user) {
            this.to_user = to_user;
        }

        public void setCoins(int coins) {
            this.coins = coins;
        }
    }

}
