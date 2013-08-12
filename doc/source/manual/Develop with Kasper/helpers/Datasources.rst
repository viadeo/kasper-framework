Connecting to SQL databases with MyBatis
========================================

Kasper comes with a proposed usage of the `MyBatis mapping framework <http://mybatis.github.io/mybatis-3/>`_ and a tables dispatching mechanism.

*This page only concentrate some good practices we encourage in the use of MyBatis as a database access framework
you can use any database library, or use MyBatis as you prefer to use it..*

How to use MyBatis
------------------

1. import **kasper-datasource** with MyBatis dependencies
2. create an instance of **SqlSessionFactory**
    * using `MyBatis <http://mybatis.github.io/mybatis-3/java-api.html#sqlSessions>`_
    * for instance using a standard java **DataSource** instance (see `reference <http://docs.oracle.com/javase/tutorial/jdbc/basics/sqldatasources.html>`_) :
    .. code-block:: java
        :linenos:

        final SqlSessionFactoryBean sqlSessionFactory = new SqlSessionFactoryBean();
        sqlSessionFactory.setDataSource(dataSource);
4. create your mapper (see below)
5. get a reference to your mapper
6. use it !

Kasper helpers
--------------

Sharded DataSource
..................

TODO : ShardedDataSource(new DataSourceFactoryConfigurer(datasourceFile,dispatcherFile));

Session factory builder
.......................

TODO :
Kasper provides a sessionFactory builder with some configuration :
MapperBuilder.createSessionFactory(dataSource);

Mapper creation helper
......................

TODO :
MapperBuilder.createMapper(sessionFactory, mapperClass)

How to create a mapper
----------------------

see `MyBatis JAVA API <http://mybatis.github.io/mybatis-3/java-api.html> `_ for additional information about creating mappers.

Kasper encourage you to use the following way to create mappers :

* put you class into package **<base>.<domain>.<area>.data.mapper**
* name your class after the pattern <table name>**Mapper**
* put a **Record** inline class within your mapper, unless this record must be shared with other mappers
* use MyBatis annotations instead of XML declarative files

**ex:**

.. code-block:: java
    :linenos:

    public interface DCRAccountMapper {

        public class Record {
            private int memberId;
            private Date creationDate;
            private Date expirationDate;
            private DateTime lastConnection;

            public Record() { }

            public Record(final int memberId, final Date creationDate, final Date expirationDate, final DateTime lastConnection) {
                this.memberId = memberId;
                this.creationDate = creationDate;
                this.expirationDate = expirationDate;
                this.lastConnection = lastConnection;
            }

            public int  getMemberId()                                     { return memberId; }
            public void setMemberId(final int memberId)                   { this.memberId = memberId; }

            public Date getCreationDate()                                 { return creationDate; }
            public void setCreationDate(final Date creationDate)          { this.creationDate = creationDate; }

            public Date getExpirationDate()                               { return expirationDate; }
            public void setExpirationDate(final Date expirationDate)      { this.expirationDate = expirationDate; }

            public DateTime getLastConnection()                           { return lastConnection; }
            public void setLastConnection(final DateTime lastConnection)  { this.lastConnection = lastConnection; }

        }

        @Insert("INSERT INTO DCRAccount (memberId, creationDate, expirationDate, lastConnection) "
              + "VALUES (#{memberId}, #{creationDate}, #{expirationDate},#{lastConnection})")
        public int create(Record ent);

        @Update("UPDATE DCRAccount SET creationDate=#{creationDate}, expirationDate=#{expirationDate}, lastConnection=#{lastConnection} "
              + "WHERE memberId=#{memberId}")
        public int update(Record ent);

        @VisibleForTesting
        @Delete("DELETE FROM DCRAccount where memberId=#{memberId}")
        public int delete(@Param("memberId") int memberId);

        @Select("SELECT * FROM DCRAccount where memberId=#{memberId}")
        public Record find(@Param("memberId") int memberId);

    }

Mapper requests samples
-----------------------

**Prepared or inlined argument**

Note that you can either use an inline parameter or prepared parameter.
If you use the #name the query statement is created as a prepared statement. If you use ‘${name}’ the parameters are inlined

.. code-block:: java
    :linenos:

    // This example creates a prepared statement, something like select * from member where email1 = ?;
    @Select("Select * from member where email1 = #{email1}")
    MemberVO selectMemberForGivenEMail(String email1);

    // This example creates n inlined statement, something like select * from member where email1 = 'someEmail';
    @Select("Select * from member where email1 = '${email1}')
    MemberVO selectMemberForGivenEMail(String email1);

**Insert a record and retrieve the ID (Key) generated by the database.**

.. code-block:: java
    :linenos:

    @Insert("insert into VisitingCardSharingRights (ownerID, viewerID, persoRights, proRights) values (#{ownerId}, #{viewerID}, #{persoRights}, #{proRights})")
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "visitingCardSharingRightsID", before = false, resultType = Integer.class)
    int insertOne(VisitingCardSharingRights record);

The **@SelectKey** annotation tells MyBatis how to determine the key, according to the database mecanism. In our example, this is for mySql.
See the documentation for more possibilities *(before=false, ...)*
The mecanism "altered" the object (named record) by calling the setter on the ID column key. *(attribute keyProperty)*

**Update a record with one parameter in the mapper's service name**

.. code-block:: java
    :linenos:

    @Update("update VisitingCardSharingRights set ownerID = #{ownerId} where VisitingCardSharingRightsID = #{visitingCardSharingRightsID}")
    int update(VisitingCardSharingRights record);

This "instruction" updates a record in the database. The mapping uses the getter names to map  (example : annoted with *#{propertyName}*, the getter must exist)

**Update a record with several parameters in the mapper's service name**

.. code-block:: java
    :linenos:

    @Update("update VisitingCardSharingRights set ownerID = #{ownerId} where VisitingCardSharingRightsID = #{id}")
    int update(@Param("id") int id, @Param("ownerId") int ownerId);

The annotation **@Param** maps parameters.

**Select a record with automatic mapping**

.. code-block:: java
    :linenos:

    @Select("select * from VisitingCardSharingRights where VisitingCardSharingRightsID = #{id}")
    VisitingCardSharingRights selectOne(Integer id);

Only attributes with setters in the VisitingCardSharingRights class will be mapped. (The getter name and the field name must be the same, case insensitive.

**Select a record with naming difference**

.. code-block:: java
    :linenos:

    @Select("select * from member where memberid = #{id}")
    @Results( {
        @Result(property="pseudo", column="nickname")
    })
    public MemberVO doSelectMemberById(int id);

In this example, we want to map the attribute name pseudo (French name) with the column name 'nickname'

**Select a record with restricted attribute**

.. code-block:: java
    :linenos:

    @Select("select memberid, nickname, firstname from member where memberid = #{id}")
    public MemberVO doSelectMemberLightById(int id);

In this example, we want to reuse the object, but we don't want to map all fields and want to keep the same domain object.
The missing attributes will not be mapped and will be initialized with the default value, usually null for strings. Take care in this kind of situation. Usually,

Recommended usage in configuration
----------------------------------

It is recommended that a common configuration/infrastructure module of your platform defines a global instance of DataSource/SqlSessionFactory and that eash of
your domain creates the instances of mappers they need. Do not hesitate to use one IOC framework with injection support.. :)

Your mappers can eventually be shared between your domains but you creates then a new coupling between them, if you can do not make this !

