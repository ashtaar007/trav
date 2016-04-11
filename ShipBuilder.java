
public class ShipBuilder {
	
	public Ship createShip(){
		Ship ship = new Ship();
		return ship;
	}
	public void addHull(Ship ship, int Tons, int bonusTL){
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
		ship.Tonnage=Tons;
		ship.ExtraTons=Tons;
		ship.components.put("Hull", new Hull());
	}
	public void addBridge(Ship ship){
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
