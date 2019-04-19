package com.evartem.backendsim

import com.evartem.domain.entity.doc.Product
import kotlin.random.Random

object RandomProductGenerator {

    fun getProducts(number: Int): MutableList<Product> =
        (1..number).map { getProduct(it) }.toMutableList()

    private fun getProduct(id: Int): Product {
        val (article, description) = articleAndDescription
        val hasSerialNumber = Random.nextBoolean()
        return Product(
            id, article, description, quantity,
            Random.nextBoolean(), hasSerialNumber,
            if (hasSerialNumber) Random.nextBoolean() else false,
            if (hasSerialNumber) Random.nextBoolean() else false,
            serialNumberPattern = if (hasSerialNumber) serialPattern else null
        )
    }

    private val quantity get() = (1..23).random()

    private val serialPattern
        get() = listOf(
            "S[A-Z\\d]{10}", null
        ).let { it[(0 until it.size).random()] }

    val articleAndDescription
        get() = listOf(
            Pair(
                "6ES7195-1GC00-0XA0",
                "SIMATIC DP, mounting rail for ET 200M, 2000 mm long, for holding bus modules for removal and insertion function"
            ),
            Pair(
                "6EP3333-8SB00-0AY0",
                "SITOP PSU8200 24 V/5 A Stabilized power supply input: 120/230 V AC, output: 24 V DC/5 A"
            ),
            Pair(
                "6EP1336-3BA10",
                "SITOP PSU8200 20 A Stabilized power supply input: 120-230 V AC 110-220 V DC output: 24 V DC/20 A"
            ),
            Pair(
                "6EP1961-3BA21",
                "SITOP PSE202U Redundancy module Input/output: 24 V DC/40 A suitable for decoupling two SITOP power supplies with maximal per 20 A output current"
            ),
            Pair(
                "6ES7971-0BA00",
                "SIMATIC S7-400, Backup battery 3.6 V/2.3 AH for PS 405 4 A/10 A/20 A and PS 407 4 A/10 A/20 A"
            ),
            Pair(
                "6GK7443-1EX30-0XE0",
                "Communications processor CP 443-1; 2x 10/100 Mbit/s (IE switch); RJ45 ports; ISO; TCP; UDP; PROFINET IO controller; S7 communication; Open communication (SEND/ RECEIVE); S7 routing; IP configuration via DHCP/ Block; IP Access Control List; Time synchronization; extended web diagnostics; Fast Startup; Support for PROFIenergy;"
            ),
            Pair(
                "6GK1716-0HB13-0AA0",
                "HARDNET-IE S7 REDCONNECT V13 incl. S7 OPC Server, S7-1613 V12 software for fail-safe S7 comm; Single License for 1 installation; Runtime software, software and electr. manual on CD-ROM, License key on USB stick, Class A; 2 languages (de, en); for 32/64 bit: Windows 7 SP1 Prof./ Ultimate, 64 bit: Windows 8.1 Pro; Windows 10; Server 2008 R2 SP1, 2012 R2 for max. 4 CP 1613 A2, CP 1623, CP 1628"
            ),
            Pair(
                "6ES7833-1CC02-0YA5",
                "SIMATIC S7, software F Systems V6.1, Floating license for 1 installation R-SW, without SW, without documentation License key on USB stick Class A, 2 languages (de,en), executable on XPPROF SP3/Server 2003 SP2/ WIN7ULT/Server 2008 R2 Reference HW: PC/PG ******************************* Content: 1x USB"
            ),
            Pair(
                "6ES7833-4CC26-0YT8",
                "SIMATIC S7, software F-Systems V6.2 Update 1 Software Media Package Data medium without license usage only in connection with valid license E-SW / R-SW, SW and documentation on CD 2 languages (de, en), executable on WIN7 SP1 / Server 2008 R2 SP1/ WIN10 / Server 2012 R2/ Server 2016 Reference HW: PC/PG"
            ),
            Pair(
                "6ES7153-4BA00-0XB0",
                "SIMATIC DP, Connection ET 200M IM 153-4 PN IO High Feature for max. 12 S7-300 modules, supports fail-safe module, HART modules, shared device, Medium Redundancy Protocol"
            ),
            Pair("6ES7953-8LF31-0AA0", "SIMATIC S7, Micro Memory Card for S7-300/C7/ET 200, 3, 3V Nflash, 64 KB"),
            Pair("6ES7321-1BL00-0AA0", "SIMATIC S7-300, Digital input SM 321, Isolated 32 DI, 24 V DC, 1x 40-pole"),
            Pair(
                "6ES7322-1BL00-0AA0",
                "SIMATIC S7-300, Digital output SM 322, isolated, 32 DO, 24 V DC, 0.5A, 1x 40-pole, Total current 4 A/group (16 A/module)"
            ),
            Pair(
                "6ES7331-1KF02-0AB0",
                "SIMATIC S7-300, Analog input SM 331, Isolated 8 AI, resolution 13 bits U/I/resistor/Pt100, NI100, NI1000, LG-NI1000, PTC/KTY, 66 ms conversion time; 1x 40-pole"
            ),
            Pair(
                "6ES7332-5HF00-0AB0",
                "SIMATIC S7-300, Analog output SM 332, isolated, 8 AO, U/I; diagnostics; resolution 11/12 bits, 40-pole, removing and inserting possible with active backplane bus"
            ),
            Pair("6ES7370-0AA01-0AA0", "SIMATIC S7-300, dummy module DM 370, Dummy module Use for module replacement"),
            Pair(
                "6ES7350-2AH01-0AE0",
                "SIMATIC S7-300, Counter module FM 350-2, 8 channels, 20 kHz, 24 V encoder for counting, frequency measurement, speed measurement, period duration measurement, dosing incl. configuration package and electronic documentation on CD-ROM"
            ),
            Pair(
                "6ES7326-1BK02-0AB0",
                "SIMATIC S7, Digital input SM 326, F-DI 24x24 V DC, Fail-safe digital input for SIMATIC S7 F-systems with diagnostic alarm, up to Category 4 (EN 954-1)/ SIL3 (IEC61508)/PLE (ISO13849), 1x 40-pole"
            ),
            Pair(
                "6ES7326-2BF10-0AB0",
                "SIMATIC S7, digital output SM 326, F-DO10x 24 V DC/2A PP, fail-safe digital output for SIMATIC S7F systems, with diagnostic alarm, LVV, up to Category 4 (EN 954-1)/ SIL3 (IEC61508)/PLE (ISO13849), 1x 40-pole"
            ),
            Pair(
                "6ES7336-4GE00-0AB0",
                "SIMATIC S7, Analog input SM 336, 6 AI; 15 bit; fail-safe analog inputs for SIMATIC Safety, with HART support, up to Category 4 (EN 954-1)/ SIL3 (IEC61508)/PLE (ISO13849), 1x 20-pole"
            ),
            Pair(
                "6ES7650-1AK11-7XX0",
                "SIMATIC PCS7, red. F-DI MTA interface module for redundant connection of the ET200M fail-safe module 6ES7326-1BK02-0AB0 12/24 channels, individually fused, redundant power supply with fusing, LED displays Measuring range: 24 V DC"
            ),
            Pair(
                "A5E01568887",
                "SIMATIC PCS7, DI16/F-DI24 MTA, 20 fuses (channel) 50 mA / 125 V for MTA: DI16: 6ES7650-1AC10-3XX0 DI16: 6ES7650-1AC11-3XX0 F-DI24: 6ES7650-1AK10-7XX0 F-DI24: 6ES7650-1AK11-7XX0"
            ),
            Pair(
                "A5E01568882",
                "SIMATIC PCS7, AO8/DI16/F-DI24/ RO10 MTA, 20 fuses (module) 1 A / 250 V for MTA: AO8: 6ES7650-1AB50-2XX0 AO8: 6ES7650-1AB51-2XX0 DI16: 6ES7650-1AC10-3XX0 DI16: 6ES7650-1AC11-3XX0 F-DI24: 6ES7650-1AK10-7XX0 F-DI24: 6ES7650-1AK10-7XX0 RO10: 6ES7650-1AM30-6XX0 RO10: 6ES7650-1AM31-6XX0"
            ),
            Pair(
                "6ES7650-1AM31-6XX0",
                "SIMATIC PCS7, red. F-DO10 relay MTA, interface module for redundant connection of the ET200M F-DO10 6ES7326-2BF10-0AB0, 10 channels, relay, redundant supply with fusing, LED displays Relay output: 24-150 V DC/120-220 V AC"
            ),
            Pair(
                "A5E01568889",
                "SIMATIC PCS7, R10/F-AI6 MTA, 20 fuses (channel) 0.1 A / 125 V for MTA: R10: 6ES7650-1AM30-6XX0 R10: 6ES7650-1AM31-6XX0 F-AI6: 6ES7650-1AH61-5XX0"
            ),
            Pair(
                "A5E02164558",
                "SIMATIC PCS7, FRO MTA, 2 relays, 12 A / 250 V AC for MTA: FRO10: 6ES7650-1AM30-6XX0 FRO10: 6ES7650-1AM31-6XX0 2 units per packing unit"
            ),
            Pair(
                "6ES7922-3BD00-0AS0",
                "Cable with front connector for SIMATIC S7-300 40-pole (6ES7392-1AM00-0AA0) on 50 pole D-sub socket for ET 200M; 0.14 mm2 Length = 3 m"
            ),
            Pair(
                "6ES7392-1AJ00-0AA0",
                "SIMATIC S7-300, Front connector for signal modules with screw contacts, 20-pole"
            ),
            Pair("6ES7392-1AM00-0AA0", "SIMATIC S7-300, Front connector with screw contacts, 40-pole"),
            Pair(
                "6ES7195-7HB00-0XA0",
                "SIMATIC DP, Bus module for ET 200M for holding two 40 mm wide I/O modules for removal and insertion function"
            ),
            Pair(
                "6ES7195-7HC00-0XA0",
                "SIMATIC DP, Bus module for ET 200M for holding an 80 mm wide I/O module for removal and insertion function"
            ),
            Pair(
                "6ES7195-7HA00-0XA0",
                "SIMATIC DP, bus module f. ET200M for holding a PS and an IM153 for the function Pulling and plugging during RUN operating mode incl. bus module cover"
            ),
            Pair(
                "6XV1840-2AH10",
                "Industrial Ethernet FC TP Standard cable, GP 2x2 (PROFINET Type A), TP installation cable for connection to IE FC RJ45 2x2, for universal use, 4-core, shielded CAT 5E, sold by the meter, delivery length max. 4500 m minimum order 20 m"
            ),
            Pair(
                "6GK1901-1BB10-2AE0",
                "Industrial Ethernet FastConnect RJ45 plug 180 2x 2, RJ45 plug-in connector (10/100 Mbit/s) with rugged metal enclosure and FC connection system, for IE FC Cable 2x 2; 180° cable outlet 1 pack = 50 units"
            ),
            Pair(
                "6GK5008-0GA00-1AB2",
                "SCALANCE XB008G unmanaged Industrial Ethernet Switch for 10/100/1000 Mbit/s; for setting up small star and line topologies; LED diagnostics, IP20, 24 V DC power supply, with 8x 10/100/1000 Mbit/s RJ45 ports electrical Manual available as a download"
            ),
            Pair(
                "6GK5208-0BA10-2AA3",
                "SCALANCE X208, managed IE switch, 8x 10/100 Mbit/s RJ45 ports, LED diagnostics, error-signaling contact with set button, redundant power supply, PROFINET IO device, network management, Redundancy Manager integrated, incl. electron. manual on CD-ROM, C-plug optional"
            ),
            Pair(
                "6ES7660-4DL18-2AD0",
                "SIMATIC PCS 7 Industrial Workstation IPC547E; (Rack PC, 19\", 4HU), Interfaces: 2x Gbit LAN (RJ45), 1x DVI-I, 2x Display Port; 1x COM, 2x PS/2, Audio; 2x USB 3.0; 6x USB 2.0 on the rear, 2x USB 3.0, on front; 1x USB 2.0 internal; 7 slots: 2x PCIE X16, 1x PCIE x8, 4x PCI; Temperature and fan monitoring Watchdog; card retainer; Core I5-4570S  (4C/4T, 2.9(3.6) GHz), 6 MB cache, IAMT); ES/OS Single Station; 240 GB SSD (EMLC) SATA, internal; 8 GB DDR3 SDRAM (2x 4 GB), Dual Channel; Without additional system bus communication cards; PCS 7 V8.2 pre-installed Windows 7 Ultimate, 64 bit, MUI (en/de/fr/it/es/cn); enclosure unpainted; DVD +/-RW (slim); 110/240V industrial power supply unit; with NAMUR; Power supply cable Europe"
            ),
            Pair(
                "6ES7660-6EG18-2ED6",
                "SIMATIC PCS 7 Industrial Workstation IPC847D; (Rack PC, 19\", 4HU), Interfaces: 2x Gbit LAN (RJ45), 1x DVI-I, 2x Display Port; 1x COM, 2x PS/2, Audio; 2x USB 3.0; 2x USB 2.0 on rear, 1x USB 3.0; 1x USB 2.0, on front; 1x USB 3.0 internal; Temperature and fan monitoring Watchdog; card retainer; Core I5-4570TE (2C/4T, 2.7 GHz, 4 MB cache, TB, VT-D, AMT); OS Server; RAID1, 1 TB (2x 1 TB HDD SATA, data mirroring); + 1TB (1x 1TB HDD as hot spare in removable frame, hot swap; on front; 8 GB DDR3 SDRAM (2 x 4 GB), Dual Channel; Without additional system bus communication cards; PCS 7 V8.2 SP1 pre-installed Windows Server 2012 R2 Standard edition incl. 5 clients, 64 bit, MUI (en/de/fr/it/es/cn); Bus module 11 slots: 3x PCI, 3x PCIE X4, 5X PCIE X16, DVD +/-RW (slim); 2 x 110/240 V redundant power supply unit; Without power supply cable"
            ),
            Pair(
                "6GK1162-3AA00",
                "Communications processor CP 1623 PCI Express X1 (3, 3 V/12 V) for connection to Industrial Ethernet (10/100/1000 Mbit/s) with 2-port switch (RJ45) via HARDNET-IE S7 and S7-REDCONNECT Operating system support see SIMATIC NET software released as of SIMATIC NET CD V7.0 HF1"
            ),
            Pair(
                "6ES7653-2BA00-0XB5",
                "SIMATIC PCS 7, software, Runtime License AS (PO 100), Floating license for 1 user R-SW, without SW, without documentation License Key on USB stick, Class A, Reference HW: PCS 7 IPC Bundle, PCS 7 Box Bundle"
            ),
            Pair(
                "6ES7658-2XA00-0XB0",
                "SIMATIC PCS 7, software Runtime License OS (PO 100) Single license for 1 installation R-SW, without SW, without documentation License Key on USB stick, Class A, Reference HW: PCS 7 IPC Bundle, PCS 7 Box Bundle"
            ),
            Pair(
                "6ES7658-2CX28-0YB5",
                "SIMATIC PCS 7, software, OS Software Client V8.2 Floating license for 1 user R-SW, without SW, without documentation License Key on USB stick, Class A, 5 languages (de, en, fr, it, es), executable on WIN7ULT Reference HW: PCS 7 IPC Bundle"
            ),
            Pair(
                "6ES7658-2BA28-0YA0",
                "SIMATIC PCS 7, software, OS Software Server V8.2 (PO 100) Single license for 1 installation R-SW, without SW, without documentation License Key on USB stick, Class A, 5 languages (de, en, fr, it, es), executable on Server 2008 R2/Server 2012 R2 Reference HW: PCS 7 IPC Bundle"
            ),
            Pair(
                "6ES7658-5AX28-0YA5",
                "SIMATIC PCS 7, software, AS/OS Engineering V8.2 floating license for 1 user E-SW, without SW, without documentation License key on USB stick, Class A, 5 languages (de, en, fr, it, es), executable on WIN7ULT/Server 2008 R2/ Server 2012 R2 Reference HW: PCS 7 IPC Bundle"
            ),
            Pair(
                "6ES7654-6CL03-4KF0",
                "SIMATIC PCS 7 CPU 410 Single AS pre-mounted and checked; with CPU 410-5H Process Automation Incl. AS RT PO 100; with system expansion card PO 500; With 1x CP443-1 IE (XTR); with UR2 (9 slots) Steel rack; With 2x 24 V DC 10 A red. Power supply (XTR); Bundle Release F: Without IL Runtime License Without CP443-5EXT (PROFIBUS);"
            ),
            Pair(
                "6ES7410-5HX08-0AB0",
                "SIMATIC PCS 7, CPU 410-5H Process Automation, central processing unit for S7-400 and S7-400H/F/FH, 5 interfaces: 2x PN, 1x DP, 2x for sync modules for using as spare part, without System Expansion Card"
            ),
            Pair(
                "6ES7656-6CC33-2KF0",
                "SIMATIC PCS 7 CPU 410 Redundancy automation system pre-mounted and checked; with 2x CPU 410-5H Process Automation Incl. AS RT PO 100; with 2 x system expansion cards PO 500, with F-runtime license; With 2x 2 10 m sync mod. XTR and 2x 1 m FOC; With 2x CP443-1 IE (XTR); With 1x UR2-H XTR (2 x 9 slots) Steel rack; With 2x 2 24 V DC 10 A red. Power supply (XTR); Bundle Release F: Without IL Runtime License Without CP443-5EXT (PROFIBUS);"
            ),
            Pair(
                "7MH4900-2AK65",
                "CONFIGURATION PACKAGE SIWAREX FTA APL FOR SIMATIC PCS7 V8.0 UPDATE 1 - HSP FOR S7-INTEGRATION - CFC-FUNCTION BLOCK - FACEPLATE FOR WINCC - PC-SOFTWARE SIWATOOL FTA - MANUAL ; ON CD-ROM"
            ),
            Pair(
                "7MH4900-2AA01",
                "SIWAREX FTA WEIGHING ELECTRONIC WITH VERIFICATION CAPABILITY.FOR AUTOMATIC AND NON AUTOMATIC             . WEIGHING APPLICATIONS. USABLE FOR S7-300 AND ET200M. EC-CERTIFICATE OF CONFORMITY 3 X 6000D.                    . APPLICATIONS:                 . RAPID DOSING, FILLING/BAGGING AND LOADING. ATTENTION: FOR LEGAL FOR TRADE APPLICATIONS PLEASE FOLLOW THE LOCAL LAWS APPLYING  IN THE COUNTRY OF LEGAL VERIFICATION ! - APPROPRIATE MMC-CARD: ONLY FOR LEGAL-FOR-TRADE APPLICATIONS R76, R51 AND R107: 7MH4900-2AY20"
            ),
            Pair(
                "6AG4012-1AA21-0BX0",
                "SIMATIC IPC347E (Rack PC, 19\", 4HU); Pentium Dual Core 3420 (2C/2T, 3.2 GHz, 3 MB cache); Interfaces: 2x Gbit Ethernet, 1x DVI-D, 1x VGA, 2x USB on the front, 4x USB on the rear, 1x USB internal, 2x serial, 2x PS/2, audio; 1x 500 GB HDD, internal; 4 GB DDR3 SD-RAM (1x 4 GB), single channel; DVD +/-RW; Windows 7 Ultimate 64 bit SP1; 100/240 V industrial power supply, without line cable"
            ),
            Pair(
                "6AV6371-1CB07-4AX0",
                "WinCC/User-Archives V7.4, Option for WinCC V7.4, runtime software, Single license, License key on USB stick"
            ),
            Pair("5SL6206-7", "Miniature circuit breaker 400 V 6kA, 2-pole, C, 6A"),
            Pair("5SL6210-7", "Miniature circuit breaker 400 V 6kA, 2-pole, C, 10 A"),
            Pair("5SL6216-7", "Miniature circuit breaker 400 V 6kA, 2-pole, C, 16A"),
            Pair("5SU1356-7KK10", "RCBO, 6 kA, 1P+N, type A, 30 mA, C-Char, In: 10 A, Un AC: 230 V"),
            Pair("5ST3705", "Pin busbar touch-safe, 16 mm2 2-phase, 1016 mm long can be cut, without end caps"),
            Pair("5ST3750", "End cap for pin busbars 2-3-phase"),
            Pair("5SY4232-8", "Miniature circuit breaker 400 V 10kA, 2-pole, D, 32 A, D=70 mm")
        ).let {
            it[(0 until it.size).random()]
        }
}