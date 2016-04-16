class ShipWeapon {	
}
class MissileWeapon extends ShipWeapon {
	static int[] flightTime = {-99,-99,1,1,1,3,5};
}
class Torpedo extends MissileWeapon{
}
class BeamWeapon extends ShipWeapon {
}
class Laser extends BeamWeapon{
}
class Sandcaster extends ShipWeapon{
	static int[] rangeModifiers = {-2,0,-2,-99,-99,-99,-99};
}
class MesonGun extends ShipWeapon{
	static int damageDiceBay = 5;
	static int[] rangeModifiers = {-2,-1,-1,0,-1,-1,-2};
	static double MCrBay = 50.0;
}
class BeamLaser extends Laser{
	static int damageDiceTurret = 1;
	static int[] rangeModifiers = {-2,-1,-1,0,-1,-1,-2};
	static double MCrTurret = 1.0;
	static int turretSlotsRequired = 1;
}
class PulseLaser extends Laser{
	static int damageDiceTurret = 2;
	static int[] rangeModifiers = {-3,-3,-2,-3,-4,-5,-99};
	static double MCrTurret = .5;
	static int turretSlotsRequired = 1;
}

class ParticleBeam extends BeamWeapon{
	static int damageDiceTurret = 3;
	static int damageDiceBarbette = 4;
	static int damageDiceBay = 6;
	static int[] rangeModifiers = {-3,-2,-1,-1,0,-1,-1};
	static double MCrTurret = 4.0;
	static double MCrBarbette = 8.0;
	static double MCrBay =20.0;
	static int turretSlotsRequired = 3;
}

