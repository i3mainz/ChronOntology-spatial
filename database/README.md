# Database structure

* name: Chronontology

## geometry processing and queries

* ST_asgeojson
* ST_EXTENT
* ST_BUFFER
* ST_CONTAINS
* ST_CENTROID
* de.i3mainz.database.PostGIS

## timeconceptsearch

* schema: dumpapi
* import via CO_DumpToSQL (archne csv -> sql && pleiades csv(s) -> sql)

### structure

* id: serial
* timeconcept: character varying
* internalid: character varying
* opttype: character varying
* optdesc: character varying
* importdate: character varying
* geom: geometry

### tables

```sql
CREATE TABLE dumpapi.arachne
(
  id serial NOT NULL,
  timeconcept character varying,
  internalid character varying,
  opttype character varying,
  optdesc character varying,
  importdate character varying,
  geom geometry,
  CONSTRAINT arachne_pkey PRIMARY KEY (id)
)
```

```sql
CREATE TABLE dumpapi.pleides
(
  id serial NOT NULL,
  timeconcept character varying,
  internalid character varying,
  opttype character varying,
  optdesc character varying,
  importdate character varying,
  geom geometry,
  CONSTRAINT arachne_pkey PRIMARY KEY (id)
)
```
