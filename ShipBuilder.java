
public class ShipBuilder {
	
	public Ship createShip(){
		Ship ship = new Ship();
		return ship;
	}
	public void addHull(Ship ship, int Tons, int TL){
		if(Tons%100!=0||Tons<100||Tons>2000||(Tons>1000&&Tons%200!=0)){
			System.out.println("Hull tonnage out of bounds.");
			return;
		}
		double MCr;
		int t = Tons/100;
		if(Tons==100)MCr=2;
		else if(200<=Tons && Tons<=300)MCr=t*4;
		else if(400<=Tons && Tons<=800)MCr=t*16-48;
		else MCr=t*10;
		System.out.println("Hull Tons: "+ Tons+" MCr: "+MCr);
		ship.MCr += MCr;
		ship.HullBaseCost = MCr;
		ship.HullBaseTons=Tons;
		ship.ExtraTons=Tons;
		ship.components.put("Hull", new Hull());
	}
	public void addArmor(Ship ship, String type, int armorValue){
		double MCr=0;
		double Tons=0;
		double percentTons=0;
		double costPerFivePercent=0;
		double protectionPerFivePercent=0;
		switch(type){
		case "Titanium Steel":protectionPerFivePercent=2;costPerFivePercent=5;break;
		case "Crystaliron":protectionPerFivePercent=4;costPerFivePercent=20;break;
		case "Bonded Superdense":protectionPerFivePercent=6;costPerFivePercent=50;break;
		default: System.out.println("Invalid armor type.");break;
		}
		percentTons = armorValue/protectionPerFivePercent*.05;
		MCr = armorValue/protectionPerFivePercent*costPerFivePercent*.01*ship.HullBaseCost;
		Tons = percentTons*ship.HullBaseTons;
		ship.MCr += MCr;
		ship.ExtraTons-=Tons;
		ship.components.put("Armor", new Armor());
		
		
	}
	public void addBridge(Ship ship){
		int Tons;
		double MCr=ship.HullBaseTons/200;
		if(100<=ship.HullBaseTons&&ship.HullBaseTons<=200){
			Tons = 10;
		}
		else if(300<=ship.HullBaseTons&&ship.HullBaseTons<=1000){
			Tons = 20;
		}
		else if(1100<=ship.HullBaseTons&&ship.HullBaseTons<=2000){
			Tons = 40;
		}
		else {
			Tons = 60;
			System.out.println("Tonnage is too high for spacecraft.");
		}
		ship.MCr += MCr;
		ship.ExtraTons-=Tons;
		ship.components.put("Bridge", new Bridge());
	}
	public void addJDrive(Ship ship, char driveCode, int bonusTL){
		int rowNum = getRowNumber(driveCode);
		int Tons;
		double MCr;
		Tons = 5+5*rowNum;
		MCr = 10*rowNum;
		System.out.println("Tons: "+ Tons+" MCr: "+MCr);
		ship.MCr += MCr;
		ship.ExtraTons-=Tons;
		ship.components.put("JDrive", new JDrive());
	}
	public void addMDrive(Ship ship, char driveCode, int bonusTL){
		int rowNum = getRowNumber(driveCode);
		int Tons;
		double MCr;
		if (rowNum==1) Tons=2;
		else Tons = -1+2*rowNum;
		MCr = 4*rowNum;
		System.out.println("Tons: "+ Tons+" MCr: "+MCr);
		ship.MCr += MCr;
		ship.ExtraTons-=Tons;
		ship.components.put("MDrive", new MDrive());
	}
	public void addPPlant(Ship ship, char driveCode, int bonusTL){
		int rowNum = getRowNumber(driveCode);
		int Tons;
		double MCr;
		Tons = 1+3*rowNum;
		MCr = 8*rowNum;
		if (MCr == 184) MCr = 182;
		System.out.println("Tons: "+ Tons+" MCr: "+MCr);
		ship.MCr += MCr;
		ship.ExtraTons-=Tons;
		ship.components.put("PPlant", new PPlant());
	}
	public void addComputer(Ship ship, int model, boolean bis){
		double MCr;
		switch(model){
			case 1:MCr = .03;break;
			case 2:MCr = .16;break;
			case 3:MCr = 2;break;
			case 4:MCr = 5;break;
			case 5:MCr = 10;break;
			case 6:MCr = 20;break;
			case 7:MCr = 30;break;
			default:MCr=0; System.out.println("Invalid computer model.");break;
		}
		if(bis)MCr*=1.5;
		ship.MCr += MCr;
		ship.components.put("Computer", new Computer());
	}
	public void addSensors(Ship ship, int modifier){
		double MCr;
		double Tons;
		switch(modifier){
			case -4:MCr=0;Tons=0;break;
			case -2:MCr=.05;Tons=1;break;
			case 0:MCr=1;Tons=2;break;
			case 1:MCr=2;Tons=3;break;
			case 2:MCr=4;Tons=5;break;
			default:MCr=0;Tons=0; System.out.println("Invalid sensors modifier.");break;
		}
		ship.MCr += MCr;
		ship.ExtraTons-=Tons;
		ship.components.put("Sensors", new Sensors());
	}
	public static int getRowNumber(char driveCode){
		int rowNum = (int)(driveCode)-64;
		if(rowNum<1||rowNum>26||driveCode=='I'||driveCode=='O'){
			System.out.println("JDrive input Out of Bounds");
			return -99;
		}
		if(rowNum>9) rowNum--;
		if(rowNum>13) rowNum--;
		return rowNum;
	}
}
