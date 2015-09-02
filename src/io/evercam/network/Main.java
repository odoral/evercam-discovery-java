package io.evercam.network;

import io.evercam.network.discovery.DiscoveredCamera;
import io.evercam.network.discovery.NetworkInfo;
import io.evercam.network.discovery.ScanRange;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

public class Main
{
	/**
	 * Discover all cameras in local network and print them in console
	 * 
	 * @param args
	 *            pass parameter -v/--verbose to enable verbose logging
	 */
	public static void main(String[] args)
	{
		// InputStreamReader inputStream = new InputStreamReader(System.in);
		// BufferedReader keyboardInput = new BufferedReader(inputStream);
		//
		// String routerIp = "", subnetMask = "";
		// try
		// {
		// System.out.println("Please enter router IP: eg. 10.0.0.1");
		// routerIp = keyboardInput.readLine();
		//
		// System.out.println("Please enter subnet mask: eg. 255.255.255.0");
		// subnetMask = keyboardInput.readLine();
		// }
		// catch (IOException e)
		// {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		if (args.length > 0)
		{
			if (args[0].equals("-v") || args[0].equals("--verbose"))
			{
				Constants.ENABLE_LOGGING = true;
			}
		}

		String routerIp = NetworkInfo.getLinuxRouterIp();
		String subnetMask = NetworkInfo.getLinuxSubnetMask();

		// String deviceIp = "";
		// String subnetMask = "";
		// try
		// {
		// NetworkInterface networkInterface =
		// NetworkInfo.getNetworkInterfaceByName("wlan0");
		// deviceIp = NetworkInfo.getIpFromInterface(networkInterface);
		// subnetMask =
		// IpTranslator.cidrToMask(NetworkInfo.getCidrFromInterface(networkInterface));
		// }
		// catch (Exception e)
		// {
		// // TODO: handle exception
		// }
		EvercamDiscover.printLogMessage("Router IP address: " + routerIp + " subnet mask: "
				+ subnetMask);
		EvercamDiscover.printLogMessage("Scanning...");

		try
		{
			ScanRange scanRange = new ScanRange(routerIp, subnetMask);

			ArrayList<DiscoveredCamera> cameraList = new EvercamDiscover().withDefaults(true)
					.discoverAllLinux(scanRange);

			EvercamDiscover.printLogMessage("Scanning finished, found " + cameraList.size()
					+ " cameras");

			printAsJson(cameraList);

			EvercamDiscover.printLogMessage("On normal completion: 0");
			System.exit(0);
		}
		catch (Exception e)
		{
			if(Constants.ENABLE_LOGGING)
			{
				e.printStackTrace();
			}
			EvercamDiscover.printLogMessage("On error: 1");
			System.exit(1);
		}
	}

	public static void printAsJson(ArrayList<DiscoveredCamera> cameraList)
	{
		if (cameraList != null)
		{
			JSONArray jsonArray = new JSONArray();

			for (DiscoveredCamera camera : cameraList)
			{
				JSONObject jsonObject = camera.toJsonObject();
				jsonArray.put(jsonObject);
			}

			JSONObject arrayJsonObject = new JSONObject().put("cameras", jsonArray);

			System.out.println(arrayJsonObject.toString(4));
		}
	}
}
