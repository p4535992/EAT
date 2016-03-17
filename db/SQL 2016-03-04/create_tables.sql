CREATE TABLE geodb.geodocument_2015_09_18 (
	doc_id int(10) NOT NULL auto_increment,
	url varchar(500),
	regione varchar(255),
	provincia varchar(255),
	city varchar(255),
	indirizzo varchar(255),
	iva varchar(255),
	email varchar(255),
	telefono varchar(255),
	fax varchar(255),
	edificio varchar(2000),
	latitude double,
	longitude double,
	nazione varchar(255),
	description varchar(10000),
	indirizzoNoCAP varchar(255),
	postalCode varchar(255),
	indirizzoHasNumber varchar(255),
	PRIMARY KEY (doc_id)
) ENGINE=InnoDB;
CREATE TABLE geodb.infodocument_2015_09_18 (
	doc_id int(10) NOT NULL auto_increment,
	url varchar(500),
	regione varchar(255),
	provincia varchar(255),
	city varchar(255),
	indirizzo varchar(255),
	iva varchar(255),
	email varchar(255),
	telefono varchar(255),
	fax varchar(255),
	edificio varchar(2000),
	latitude double,
	longitude double,
	nazione varchar(255),
	description varchar(10000),
	indirizzoNoCAP varchar(255),
	postalCode varchar(255),
	indirizzoHasNumber varchar(255),
	identifier varchar(1000),
	name_location varchar(1000),
	PRIMARY KEY (doc_id)
) ENGINE=InnoDB;
CREATE TABLE geodb.offlinesite (
	id int(10),
	url varchar(1000)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
CREATE TABLE geodb.websitehtml (
	id int(10) NOT NULL auto_increment,
	url mediumtext,
	file_path mediumtext,
	processing_status tinyint(3) DEFAULT 0,
	PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;