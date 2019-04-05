package com.evartem.data.remote

object TestData1
{
    val JSON_DATA1 = """
[
	{
		"id": "be7aa229-7d5e-4f90-bd7a-e437ecb6f9de",
		"number": 7,
		"date": "12.03.2019",
		"seller": "Siemens AG",
		"scanCopyUrl": "http://somewebsiteurl.biz/scans/be7aa229-7d5e-4f90-bd7a-e437ecb6f9de.jpg",
		"comment": "Unsealed packagings are OK",
		"products": [
			{
				"id": 1,
				"article": "6ES7515-2AM01-0AB0",
				"description": "SIMATIC S7-1500, CPU 1515-2 PN, Central processing unit with work memory 500 KB for Program and 3 MB for data, 1st interface: PROFINET IRT with 2-port switch, 2nd interface: PROFINET RT, 30 ns bit performance, SIMATIC Memory Card required",
				"quantity": 1,
				"articleScanRequired": true,
				"hasSerialNumber": true,
				"serialNumberScanRequired": true,
				"equalSerialNumbersAreOk": false,
				"serialNumberPattern": "S[A-Z\d]{10}"
			},
			{
				"id": 2,
				"article": "6ES7531-7KF00-0AB0",
				"description": "SIMATIC S7-1500, analog input module AI 8xU/I/RTD/TC ST, 16 bit resolution, accuracy 0.3%, 8 channels in groups of 8, 4 channels for RTD measurement, common mode voltage 10 V; Diagnostics, hardware interrupts Incl. infeed element, shield bracket and shield terminal: Front connector (screw terminals or push-in) to be ordered separately",
				"quantity": 2,
				"articleScanRequired": true,
				"hasSerialNumber": true,
				"serialNumberScanRequired": true,
				"equalSerialNumbersAreOk": false,
				"serialNumberPattern": "S[A-Z\d]{10}"
			},
			{
				"id": 3,
				"article": "6ES7521-1BH00-0AB0",
				"description": "SIMATIC S7-1500, digital input module DI 16xDC 24V HF, 16 channels in groups of 16; input delay 0.05..20 ms; Input type 3 (IEC 61131); Diagnostics, hardware interrupts: Front connector (screw terminals or push-in) to be ordered separately",
				"quantity": 4,
				"articleScanRequired": true,
				"hasSerialNumber": true,
				"serialNumberScanRequired": true,
				"equalSerialNumbersAreOk": false,
				"serialNumberPattern": "S[A-Z\d]{10}"
			},
			{
				"id": 4,
				"article": "3UF7011-1AB00-0",
				"description": "Basic unit SIMOCODE pro V PN, Ethernet/PROFINET IO, PN system redundancy, OPC UA server, Web server, transmission rate 100 Mbps, 2 x bus connection via RJ45, 4I/3O freely parameterizable, Us: 24 V DC, input for thermistor connection Monostable relay outputs, expandable by extension modules",
				"quantity": 2,
				"articleScanRequired": true,
				"hasSerialNumber": true,
				"serialNumberScanRequired": false,
				"equalSerialNumbersAreOk": true
			}
		]
	},
	{
		"id": "666174ef-0963-4f11-87b1-677adb820455",
		"number": 9,
		"date": "16.03.2019",
		"seller": "Phoenix Contact RUS",
		"products": [
			{
				"id": 1,
				"article": "2891001",
				"description": "Ethernet switch, 5 TP RJ45 ports, automatic detection of data transmission speed of 10 or 100 Mbps (RJ45), autocrossing function",
				"quantity": 2,
				"articleScanRequired": false,
				"hasSerialNumber": true,
				"serialNumberScanRequired": true,
				"equalSerialNumbersAreOk": false
			},
			{
				"id": 2,
				"article": "2891002",
				"description": "Ethernet switch, 8 TP RJ45 ports, automatic detection of data transmission speed of 10 or 100 Mbps (RJ45), autocrossing function",
				"quantity": 4,
				"articleScanRequired": false,
				"hasSerialNumber": true,
				"serialNumberScanRequired": true,
				"equalSerialNumbersAreOk": false,
				"results": [
					{
						"id": 1,
						"status": 1,
						"serial": "PC2019-178654"
					},
					{
						"id": 2,
						"status": 0,
						"comment": "Blurred serial number barcode"
					},
					{
						"id": 3,
						"status": 1,
						"comment": "The box has multiple dents"
					}
				]
			}
		]
	}
]
    """
}