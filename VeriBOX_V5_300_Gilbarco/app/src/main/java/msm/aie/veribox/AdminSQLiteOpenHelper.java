package msm.aie.veribox;

/**
 * Created by Ing Miguel Santiago on 22/02/18.
 * Clase alta de Base de Datos e inicializacion
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class AdminSQLiteOpenHelper extends SQLiteOpenHelper {

	public AdminSQLiteOpenHelper(Context context, String nombre, CursorFactory factory, int version) {
		super(context, nombre, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table estacion(num integer primary key, " +
				"num_estacion text, " +
				"encabezado text, " +
				"pie text,"+
				"cliente_pemex text)");	
		//db.execSQL("INSERT INTO erfc (num, rfc, activo) VALUES ('1', 'desde inicio', '1') ");
		
		db.execSQL("create table config (num integer primary key, "+
				"clv text, "+		//1
				"num_tablet text, "+//2
				"mac text, "+		//3
				"version text, "+	//4
				"servidor text, "+	//5
				"imp_ext text, "+	//6
				"mac_serial text, "+//7
				"nomad text, "+ 	//8
				"pend text, "+		//9
				"vista text, "+		//10
				"rango_pos_ini text, "+ //11
				"rango_pos_fin text, "+	//12
				"clv_gerente text, "+	//13
				"medio_pago text, "+	//14
				"acu_venta text, "+		//15
				"msj_esp text, "+		//16
				"max_tck text, "+		//17
				"met_pago text, "+		//18
				"dig_pago text, "+		//19
				"carpeta text, "+		//20
				"visTienda text, "+		//21
				"visTickets text, "+	//22
				"visOperador text, "+	//23
				"visMepagoT text, "+	//24
				"visMepagoF text, "+	//25
				"visOrdenTP text, "+	//26
				"extra1 text, "+		//27
				"estadopago text, "+	//28
				"usoLector text, "+		//29
				"acumulado text, "+		//30
				"extra2 text, "+		//31 dato_usoCfdi
				"extra3 text, "+		//32 dato_TckCobro
				"extra4 text, "+		//33 dato_visFueraServ
				"extra5 text, "+		//34 dato_visCambioTur
				"extra6 text, "+		//35 dato_visInvTanque
				"extra7 text, "+		//36 dato_visRecTurno
				"extra8 text, "+		//37 dato_imp_let_max
				"extra9 text, "+		//38 dato_multiples_posiciones
				"extra10 text)");		//39

		db.execSQL("INSERT INTO config (num, mac, servidor,			num_tablet, clv, 		version, mac_serial, nomad, 		imp_ext, pend, vista,rango_pos_ini, rango_pos_fin,	clv_gerente, medio_pago, acu_venta, msj_esp, max_tck, met_pago, dig_pago, carpeta, visTienda, visTickets, visOperador, visMepagoT, visMepagoF, visOrdenTP, extra1, 	estadopago, usoLector, acumulado, extra2, extra3, extra4 , extra5, extra6, extra7, extra8, extra9 , extra10) VALUES " +
									"('1', 'mac', '192.168.001.120', '1', 		'111111', '1.6-0', 	'serial_mac', 'nomad_mac', '1', 	'0', 	'2', '1', 			'2', 			'000000',	 '0', 			'1', 	'0', 		'5', 	'', 	'', 		'00', 	'1', 		'1', 		'1', 		'1', 		'0', 		'0', 	  '00AA00AA', '0', 			'0', 	'', 		'1', 	'1', 	'0', 	'0', 	'0', 	'0', 	'1', 	'1', 	'1') ");

		//db.execSQL("INSERT INTO config (num, mac, servidor,			num_tablet, clv, 		version, mac_serial, nomad, 		imp_ext, pend, vista,rango_pos_ini, rango_pos_fin,	clv_gerente, medio_pago, acu_venta, msj_esp, max_tck, met_pago, dig_pago, carpeta, visTienda, visTickets, visOperador, visMepagoT, visMepagoF, visOrdenTP, extra1, 	estadopago, usoLector, acumulado, extra2, extra3, extra4 , extra5) VALUES " +
		//							"('1', 'mac', '192.168.001.120', '1', 		'111111', '1.6-0', 	'serial_mac', 'nomad_mac', '1', 	'0', 	'2', '1', 			'2', 			'000000',	 '0', 			'1', 	'0', 		'5', 	'', 	'', 		'00', 	'1', 		'1', 		'1', 		'1', 		'0', 		'0', 	  '00AA00AA', '0', 			'0', 	'', 		'1', 	'1', 	'1', 	'1') ");

		db.execSQL("create table pend (num integer primary key, "+
				"tipo text, "+		//
				"intentos text, "+	//Numero de intetos
				"confi, "+			//Confimacion de pendiente
				"secu text, "+		//secuendia de mensajes
				"servidor text, "+
				"imp_ext text, "+
				"mac_serial text, "+
				"nomad text, "+
				"pend text)");
		
		db.execSQL("create table users(num integer primary key, " +
				"user text, " +
				"pass text, " +
				"atributos text, "+
				"ext1 text, "+
				"ext2 text)");

        db.execSQL("create table lectores (num integer primary key, " +
                "lec text, " +
                "posc text, "+
                "ext1 text, "+
                "ext2 text, "+
                "ext3 text, "+
                "ext4 text)");

        db.execSQL("INSERT INTO lectores (num, lec, posc, ext1, ext2, ext3, ext4) VALUES " +
                                        "('1', '', '01|02', '', '', '', '')," +
										"('2', '', '01|02', '', '', '', '')," +
										"('3', '', '01|02', '', '', '', '')," +
										"('4', '', '01|02', '', '', '', '')," +
										"('5', '', '01|02', '', '', '', '') ");
    }


    @Override
	public void onUpgrade(SQLiteDatabase db, int versionAnte, int versionNue) {
		db.execSQL("drop table if exists config");
		db.execSQL("create table config (num integer primary key, "+
				"clv text, "+		//1
				"num_tablet text, "+//2
				"mac text, "+		//3
				"version text, "+	//4
				"servidor text, "+	//5
				"imp_ext text, "+	//6
				"mac_serial text, "+//7
				"nomad text, "+ 	//8
				"pend text, "+		//9
				"vista text, "+		//10
				"rango_pos_ini text, "+ //11
				"rango_pos_fin text, "+	//12
				"clv_gerente text, "+	//13
				"medio_pago text, "+	//14
				"acu_venta text, "+		//15
				"msj_esp text, "+		//16
				"max_tck text, "+		//17
				"met_pago text, "+		//18
				"dig_pago text, "+		//19
				"carpeta text, "+		//20
				"visTienda text, "+		//21
				"visTickets text, "+	//22
				"visOperador text, "+	//23
				"visMepagoT text, "+	//24
				"visMepagoF text, "+	//25
				"visOrdenTP text, "+	//26
				"extra1 text, "+		//27
				"estadopago text, "+	//28
				"usoLector text, "+		//29
				"acumulado text, "+		//30
				"extra2 text, "+		//31
				"extra3 text, "+		//32
				"extra4 text, "+		//33
				"extra5 text, "+		//34
				"extra6 text, "+		//35
				"extra7 text, "+		//36
				"extra8 text, "+		//37
				"extra9 text, "+		//38
				"extra10 text)");		//39

		db.execSQL("INSERT INTO config (num, mac, servidor,			num_tablet, clv, 		version, mac_serial, nomad, 		imp_ext, pend, vista,rango_pos_ini, rango_pos_fin,	clv_gerente, medio_pago, acu_venta, msj_esp, max_tck, met_pago, dig_pago, carpeta, visTienda, visTickets, visOperador, visMepagoT, visMepagoF, visOrdenTP, extra1, 	estadopago, usoLector, acumulado, extra2, extra3, extra4 , extra5, extra6, extra7, extra8, extra9 , extra10) VALUES " +
									"('1', 'mac', '192.168.001.120', '1', 		'111111', '1.6-0', 	'serial_mac', 'nomad_mac', '1', 	'0', 	'2', '1', 			'2', 			'000000',	 '0', 			'1', 	'0', 		'5', 	'', 	'', 		'00', 	'1', 		'1', 		'1', 		'1', 		'0', 		'0', 	  '00AA00AA', '0', 			'0', 	'', 		'1', 	'1', 	'1', 	'1', 	'1', 	'1', 	'1', 	'1', 	'1') ");

	}

}
