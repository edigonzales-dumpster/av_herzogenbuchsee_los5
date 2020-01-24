@GrabConfig(systemClassLoader=true)
@Grab(group='org.postgresql', module='postgresql', version='42.2.9')

import groovy.sql.Sql

def url = 'jdbc:postgresql://localhost:54321/edit'
def user = 'admin'
def password = 'admin'
def driver = 'org.postgresql.Driver'
def sql = Sql.newInstance(url, user, password, driver)


List liegenschaften_liegenschaft = sql.rows("""
SELECT 
    * 
FROM 
    av_herzogenbuchsee.liegenschaften_liegenschaft AS liegenschaft
    INNER JOIN av_herzogenbuchsee.t_perimeter AS perimeter
    ON ST_Intersects(ST_PointOnSurface(liegenschaft.geometrie), perimeter.geometrie)
""")

List liegenschaften_grundstueckpos = sql.rows("""
SELECT 
    * 
FROM 
    av_herzogenbuchsee.liegenschaften_grundstueckpos
WHERE
    grundstueckpos_von IN ("""+liegenschaften_liegenschaft.liegenschaft_von.join(",")+""")
""" )

List liegenschaften_grundstueckposup2 = sql.rows("""
SELECT 
    * 
FROM 
    av_herzogenbuchsee.liegenschaften_grundstueckposup2
WHERE
    grundstueckposup2_von IN ("""+liegenschaften_liegenschaft.liegenschaft_von.join(",")+""")
""" )

List liegenschaften_grundstueckposup5 = sql.rows("""
SELECT 
    * 
FROM 
    av_herzogenbuchsee.liegenschaften_grundstueckposup5
WHERE
    grundstueckposup5_von IN ("""+liegenschaften_liegenschaft.liegenschaft_von.join(",")+""")
""" )

List liegenschaften_grundstueck = sql.rows("""
SELECT 
    * 
FROM 
    av_herzogenbuchsee.liegenschaften_grundstueck
WHERE
    t_id IN ("""+liegenschaften_liegenschaft.liegenschaft_von.join(",")+""")
""" )

sql.withTransaction {
    sql.execute("""
    DELETE FROM 
        av_herzogenbuchsee.liegenschaften_liegenschaft
    WHERE
        t_id IN ("""+liegenschaften_liegenschaft.t_id.join(",")+""")
    """)

    sql.execute("""
    DELETE FROM 
        av_herzogenbuchsee.liegenschaften_grundstueckpos
    WHERE
        t_id IN ("""+liegenschaften_grundstueckpos.t_id.join(",")+""")
    """)

    sql.execute("""
    DELETE FROM 
        av_herzogenbuchsee.liegenschaften_grundstueckposup2
    WHERE
        t_id IN ("""+liegenschaften_grundstueckposup2.t_id.join(",")+""")
    """)

    sql.execute("""
    DELETE FROM 
        av_herzogenbuchsee.liegenschaften_grundstueckposup5
    WHERE
        t_id IN ("""+liegenschaften_grundstueckposup5.t_id.join(",")+""")
    """)

    sql.execute("""
    DELETE FROM 
        av_herzogenbuchsee.liegenschaften_grundstueck
    WHERE
        t_id IN ("""+liegenschaften_grundstueck.t_id.join(",")+""")
    """)
}

sql.close()
