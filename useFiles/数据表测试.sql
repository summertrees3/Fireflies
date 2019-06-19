---------------------数据表测试-------------------------------
连接hive：
beeline
!connect jdbc:hive2://hoperun.com:10000
hive/hive

-----1----------------------------------------------------

-- *****列转行（hive）
create table yp_audio_voicetime(hwplayer int,qqplayer int,xiamiplayer int)
row format delimited
fields terminated by '\t';

insert into yp_audio_voicetime values(1,2,3);
insert into yp_audio_voicetime values(4,5,6);
insert into yp_audio_voicetime values(7,8,9);

select substr(a,1,instr(a,'#')-1),sum(substr(a,instr(a,'#')+1))
from yp_audio_voicetime lateral view explode(split(concat('hwplayer','#',nvl(hwplayer,0),',','qqplayer','#',nvl(qqplayer,0),',','xiamiplayer','#',nvl(xiamiplayer,0)),',')) t as a
group by substr(a,1,instr(a,'#')-1);
+--------------+-------+--+
|     _c0      |  _c1  |
+--------------+-------+--+
| hwplayer     | 12.0  |
| qqplayer     | 15.0  |
| xiamiplayer  | 18.0  |
+--------------+-------+--+


-- *****行转列（mysql）
CREATE TABLE `student` (
  `stuid` VARCHAR(16) NOT NULL COMMENT '学号',
  `stunm` VARCHAR(20) NOT NULL COMMENT '学生姓名',
  PRIMARY KEY (`stuid`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB;

Insert Into student (stuid, stunm) Values('1001', '张三');
Insert Into student (stuid, stunm) Values('1002', '李四');
Insert Into student (stuid, stunm) Values('1003', '赵二');
Insert Into student (stuid, stunm) Values('1004', '王五');
Insert Into student (stuid, stunm) Values('1005', '刘青');

CREATE TABLE `score` (
  `stuid` VARCHAR(16) NOT NULL,
  `coursenm` VARCHAR(20) NOT NULL,
  `scores` FLOAT NULL DEFAULT NULL,
  PRIMARY KEY (`stuid`, `coursenm`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB;

Insert Into score(stuid, coursenm, scores) Values('1001', 'JAVA', 67);
Insert Into score(stuid, coursenm, scores) Values('1002', 'JAVA', 68);
Insert Into score(stuid, coursenm, scores) Values('1003', 'JAVA', 69);
Insert Into score(stuid, coursenm, scores) Values('1004', 'JAVA', 70);
Insert Into score(stuid, coursenm, scores) Values('1005', 'JAVA', 71);
Insert Into score(stuid, coursenm, scores) Values('1001', 'SCALA', 87);
Insert Into score(stuid, coursenm, scores) Values('1002', 'SCALA', 88);
Insert Into score(stuid, coursenm, scores) Values('1003', 'SCALA', 89);
Insert Into score(stuid, coursenm, scores) Values('1004', 'SCALA', 90);
Insert Into score(stuid, coursenm, scores) Values('1005', 'SCALA', 91);

SET @sql = NULL;
SELECT
 GROUP_CONCAT(DISTINCT
  CONCAT(
   'MAX(IF(s.coursenm = ''',
   s.coursenm,
   ''', s.scores, NULL)) AS ''',
   s.coursenm, ''''
  )
 ) INTO @sql
FROM score s;
 
SET @sql = CONCAT('Select st.*, ', @sql, 
            ' From student st 
            Left Join score s On st.stuid = s.stuid
            Group by st.stuid');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;


-----2----------------------------------------------------
create table xs_display_0201(device_name string,br string,brM string)
row format delimited
fields terminated by '\t';

insert into xs_display_0201 values('MHA-AL00','5','0');
insert into xs_display_0201 values('MHA-AL00','7','0');
insert into xs_display_0201 values('MHA-AL00','5','1');
insert into xs_display_0201 values('MHA-AL00','7','1');
insert into xs_display_0201 values('MHA-AL00','5','0');
insert into xs_display_0201 values('MHA-AL00','7','0');
insert into xs_display_0201 values('MHA-AL00','7','0');
insert into xs_display_0201 values('MHA-AL00','7','1');
insert into xs_display_0201 values('MHA-AL00','5','all');
insert into xs_display_0201 values('MHA-AL00','7','all');
insert into xs_display_0201 values('MHA-AL00','5','all');
insert into xs_display_0201 values('MHA-AL00','7','all');
insert into xs_display_0201 values('MHA-AL00','5','all');
insert into xs_display_0201 values('MHA-AL00','7','all');
insert into xs_display_0201 values('MHA-AL00','7','all');
insert into xs_display_0201 values('MHA-AL00','7','all');

select * from 
(select device_name,br,brM,count(1) over(partition by device_name,br,brM) as output_one,count(1) over(partition by device_name,brM) as output_two from xs_display_0201)a 
group by device_name,br,brM,output_one,output_two;
+---------------------------------+------------------------+-------------------------+--------------------------------+--------------------------------+--+
| (tok_table_or_col device_name)  | (tok_table_or_col br)  | (tok_table_or_col brm)  | (tok_table_or_col output_one)  | (tok_table_or_col output_two)  |
+---------------------------------+------------------------+-------------------------+--------------------------------+--------------------------------+--+
| MHA-AL00                        | 5                      | 0                       | 2                              | 5                              |
| MHA-AL00                        | 5                      | 1                       | 1                              | 3                              |
| MHA-AL00                        | 5                      | all                     | 3                              | 8                              |
| MHA-AL00                        | 7                      | 0                       | 3                              | 5                              |
| MHA-AL00                        | 7                      | 1                       | 2                              | 3                              |
| MHA-AL00                        | 7                      | all                     | 5                              | 8                              |
+---------------------------------+------------------------+-------------------------+--------------------------------+--------------------------------+--+


-----3----------------------------------------------------
create table xs_display_0203(device_name string,li string,brM string)
row format delimited
fields terminated by '\t';

insert into xs_display_0203 values('MHA-AL00','3','0');
insert into xs_display_0203 values('MHA-AL00','9','0');
insert into xs_display_0203 values('MHA-AL00','3','1');
insert into xs_display_0203 values('MHA-AL00','9','1');
insert into xs_display_0203 values('MHA-AL00','3','0');
insert into xs_display_0203 values('MHA-AL00','9','0');
insert into xs_display_0203 values('MHA-AL00','9','0');
insert into xs_display_0203 values('MHA-AL00','9','1');

select a.device_name,a.li,a.brM,a.output_one,b.output_two from
(select device_name,li,brM,count(*) output_one from xs_display_0203 where brM='1' group by device_name,li,brM) a
inner join 
(select device_name,li,count(*) output_two from xs_display_0203 group by device_name,li) b
on a.device_name=b.device_name and a.li=b.li;
+----------------+-------+--------+---------------+---------------+--+
| a.device_name  | a.li  | a.brm  | a.output_one  | b.output_two  |
+----------------+-------+--------+---------------+---------------+--+
| MHA-AL00       | 3     | 1      | 1             | 3             |
| MHA-AL00       | 9     | 1      | 2             | 5             |
+----------------+-------+--------+---------------+---------------+--+



-----4----------------------------------------------------
当ORDER BY后面缺少窗口从句条件，窗口规范默认是：RANGE BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW
当ORDER BY和窗口从句都缺失，窗口规范默认是：ROW BETWEEN UNBOUNDED PRECEDING AND UNBOUNDED FOLLOWING

注意:
    结果和ORDER BY相关,默认为升序
    如果不指定ROWS BETWEEN,默认为从起点到当前行;
    如果不指定ORDER BY，则将分组内所有值累加;
    PRECEDING：往前
    FOLLOWING：往后
    CURRENT ROW：当前行
    UNBOUNDED：无界限（起点或终点）
    UNBOUNDED PRECEDING：表示从前面的起点
    UNBOUNDED FOLLOWING：表示到后面的终点
    其他COUNT、AVG，MIN，MAX，和SUM用法一样。

create table orders(user_id string,device_id string,user_type string,price float,sales int)
row format delimited
fields terminated by '\t';

insert into orders values('zhangsa','test1','new',67.1,2);
insert into orders values('lisi','test2','old',43.32,1);
insert into orders values('wanger','test3','new',88.88,3);
insert into orders values('liliu','test4','new',66.0,1);
insert into orders values('tom','test5','new',54.32,1);
insert into orders values('tomas','test6','old',77.77,2);
insert into orders values('tomson','test7','old',88.44,3);
insert into orders values('tom1','test8','new',56.55,6);
insert into orders values('tom2','test9','new',88.88,5);
insert into orders values('tom3','test10','new',66.66,5);

--1--
select
    user_id,
    user_type,
    sales,
    -- 默认从起点到当前所有重复行
    sum(sales) over(partition by user_type order by sales asc) as sales_1,
    -- 从起点到当前所有重复行与sales_1结果相同
    sum(sales) over(partition by user_type order by sales asc range between unbounded preceding and current row) as sales_2,
    -- 从起点到当前行，结果与sale_1结果不同
    sum(sales) over(partition by user_type order by sales asc rows between unbounded preceding and current row) as sales_3,
    -- 当前行加上往前3行
    sum(sales) over(partition by user_type order by sales asc rows between 3 preceding and current row) as sales_4,
    -- 当前范围往上加3行
    sum(sales) over(partition by user_type order by sales asc range between 3 preceding and current row) as sales_5,
    -- 当前行+往前3行+往后1行
    sum(sales) over(partition by user_type order by sales asc rows between 3 preceding and 1 following) as sales_6,
	
    --？
    sum(sales) over(partition by user_type order by sales asc range between 3 preceding and 1 following) as sales_7,
	
    -- 当前行+之后所有行
    sum(sales) over(partition by user_type order by sales asc rows between current row and unbounded following) as sales_8,
    --
    sum(sales) over(partition by user_type order by sales asc range between current row and unbounded following) as sales_9,
    -- 分组内所有行
    sum(sales) over(partition by user_type) as sales_10
from orders
order by user_type,sales,user_id;
+----------+------------+--------+----------+----------+----------+----------+----------+----------+----------+----------+----------+-----------+--+
| user_id  | user_type  | sales  | sales_1  | sales_2  | sales_3  | sales_4  | sales_5  | sales_6  | sales_7  | sales_8  | sales_9  | sales_10  |
+----------+------------+--------+----------+----------+----------+----------+----------+----------+----------+----------+----------+-----------+--+
| liliu    | new        | 1      | 2        | 2        | 1        | 1        | 2        | 2        | 4        | 23       | 23       | 23        |
| tom      | new        | 1      | 2        | 2        | 2        | 2        | 2        | 4        | 4        | 22       | 23       | 23        |
| zhangsa  | new        | 2      | 4        | 4        | 4        | 4        | 4        | 7        | 7        | 21       | 21       | 23        |
| wanger   | new        | 3      | 7        | 7        | 7        | 7        | 7        | 12       | 7        | 19       | 19       | 23        |
| tom2     | new        | 5      | 17       | 17       | 12       | 11       | 15       | 16       | 21       | 16       | 16       | 23        |
| tom3     | new        | 5      | 17       | 17       | 17       | 15       | 15       | 21       | 21       | 11       | 16       | 23        |
| tom1     | new        | 6      | 23       | 23       | 23       | 19       | 19       | 19       | 19       | 6        | 6        | 23        |
| lisi     | old        | 1      | 1        | 1        | 1        | 1        | 1        | 3        | 3        | 6        | 6        | 6         |
| tomas    | old        | 2      | 3        | 3        | 3        | 3        | 3        | 6        | 6        | 5        | 5        | 6         |
| tomson   | old        | 3      | 6        | 6        | 6        | 6        | 6        | 6        | 6        | 3        | 3        | 6         |
+----------+------------+--------+----------+----------+----------+----------+----------+----------+----------+----------+----------+-----------+--+

--2--
select
    user_id,
    user_type,
    sales,
    ROW_NUMBER() OVER(PARTITION BY user_type ORDER BY sales) AS row_num,
    first_value(user_id) over (partition by user_type order by sales desc) as max_sales_user,
    first_value(user_id) over (partition by user_type order by sales asc) as min_sales_user,
    last_value(user_id) over (partition by user_type order by sales desc) as curr_last_min_user,
    last_value(user_id) over (partition by user_type order by sales asc) as curr_last_max_user
from
    orders
order by user_type,sales,user_id;
+----------+------------+--------+----------+-----------------+-----------------+---------------------+---------------------+--+
| user_id  | user_type  | sales  | row_num  | max_sales_user  | min_sales_user  | curr_last_min_user  | curr_last_max_user  |
+----------+------------+--------+----------+-----------------+-----------------+---------------------+---------------------+--+
| liliu    | new        | 1      | 1        | tom1            | liliu           | tom                 | tom                 |
| tom      | new        | 1      | 2        | tom1            | liliu           | tom                 | tom                 |
| zhangsa  | new        | 2      | 3        | tom1            | liliu           | zhangsa             | zhangsa             |
| wanger   | new        | 3      | 4        | tom1            | liliu           | wanger              | wanger              |
| tom2     | new        | 5      | 5        | tom1            | liliu           | tom3                | tom3                |
| tom3     | new        | 5      | 6        | tom1            | liliu           | tom3                | tom3                |
| tom1     | new        | 6      | 7        | tom1            | liliu           | tom1                | tom1                |
| lisi     | old        | 1      | 1        | tomson          | lisi            | lisi                | lisi                |
| tomas    | old        | 2      | 2        | tomson          | lisi            | tomas               | tomas               |
| tomson   | old        | 3      | 3        | tomson          | lisi            | tomson              | tomson              |
+----------+------------+--------+----------+-----------------+-----------------+---------------------+---------------------+--+

--3--
select
    user_id,
    device_id,
    sales,
    ROW_NUMBER() OVER(ORDER BY sales) AS row_num,
    lead(device_id) over (order by sales) as default_after_one_line,
    lag(device_id) over (order by sales) as default_before_one_line,
    lead(device_id,2) over (order by sales) as after_two_line,
    lag(device_id,2,'abc') over (order by sales) as before_two_line
from
    orders
order by sales;
+----------+------------+--------+----------+-------------------------+--------------------------+-----------------+------------------+--+
| user_id  | device_id  | sales  | row_num  | default_after_one_line  | default_before_one_line  | after_two_line  | before_two_line  |
+----------+------------+--------+----------+-------------------------+--------------------------+-----------------+------------------+--+
| lisi     | test2      | 1      | 1        | test4                   | NULL                     | test5           | abc              |
| liliu    | test4      | 1      | 2        | test5                   | test2                    | test1           | abc              |
| tom      | test5      | 1      | 3        | test1                   | test4                    | test6           | test2            |
| zhangsa  | test1      | 2      | 4        | test6                   | test5                    | test3           | test4            |
| tomas    | test6      | 2      | 5        | test3                   | test1                    | test7           | test5            |
| wanger   | test3      | 3      | 6        | test7                   | test6                    | test9           | test1            |
| tomson   | test7      | 3      | 7        | test9                   | test3                    | test10          | test6            |
| tom2     | test9      | 5      | 8        | test10                  | test7                    | test8           | test3            |
| tom3     | test10     | 5      | 9        | test8                   | test9                    | NULL            | test7            |
| tom1     | test8      | 6      | 10       | NULL                    | test10                   | NULL            | test9            |
+----------+------------+--------+----------+-------------------------+--------------------------+-----------------+------------------+--+

--4--
select
user_id,user_type,sales,
RANK() over (partition by user_type order by sales desc) as r,
ROW_NUMBER() over (partition by user_type order by sales desc) as rn,
DENSE_RANK() over (partition by user_type order by sales desc) as dr
from
orders;
+----------+------------+--------+----+-----+-----+--+
| user_id  | user_type  | sales  | r  | rn  | dr  |
+----------+------------+--------+----+-----+-----+--+
| tom1     | new        | 6      | 1  | 1   | 1   |
| tom2     | new        | 5      | 2  | 2   | 2   |
| tom3     | new        | 5      | 2  | 3   | 2   |
| wanger   | new        | 3      | 4  | 4   | 3   |
| zhangsa  | new        | 2      | 5  | 5   | 4   |
| liliu    | new        | 1      | 6  | 6   | 5   |
| tom      | new        | 1      | 6  | 7   | 5   |
| tomson   | old        | 3      | 1  | 1   | 1   |
| tomas    | old        | 2      | 2  | 2   | 2   |
| lisi     | old        | 1      | 3  | 3   | 3   |
+----------+------------+--------+----+-----+-----+--+

--5--
select
    user_type,sales,
    --分组内将数据分成2片
    NTILE(2) OVER(PARTITION BY user_type ORDER BY sales) AS nt2,
    --分组内将数据分成3片
    NTILE(3) OVER(PARTITION BY user_type ORDER BY sales) AS nt3,
    --分组内将数据分成4片
    NTILE(4) OVER(PARTITION BY user_type ORDER BY sales) AS nt4,
    --将所有数据分成4片
    NTILE(4) OVER(ORDER BY sales) AS all_nt4
from
    orders
order by
    user_type,
    sales;

--6--
SET @sql = NULL;
SELECT
 GROUP_CONCAT(DISTINCT
  CONCAT(
   'MAX(IF(s.paperName = ''',
   s.paperName,
   ''', s.score, NULL)) AS ''',
   s.paperName, ''''
  )
 ) INTO @sql
FROM t_info_exam s;
SET @sql = CONCAT('Select st.*, ', @sql, 
            ' From t_info_students st
            inner Join t_info_exam s On st.userId = s.paperOwnerId
            Group by st.card_id');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;


SET @sql = NULL;
SELECT
 GROUP_CONCAT(DISTINCT
  CONCAT(
   'MAX(IF(s.paperName = ''',
   s.paperName,
   ''', s.score, NULL)) AS ''',
   s.paperName, ''''
  )
 ) INTO @sql
FROM t_info_exam s where paperOwnerId in
<foreach item="cardIdIteam" collection="array" open="(" separator="," close=")">
    #{cardIdIteam}
</foreach>;
SET @sql = CONCAT('Select st.*, ', @sql, 
            ' From t_info_students st
            inner Join t_info_exam s On st.userId = s.paperOwnerId
            Group by st.card_id');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

--7--
create table students_test(
id int unsigned not null auto_increment primary key,
name char(8) not null,
gender char(4) not null,
age tinyint unsigned not null,
tel char(13) null default "-"
);

INSERT IGNORE INTO当插入数据时，在设置了记录的唯一性后，如果插入重复数据，将不返回错误，只以警告形式返回。 而REPLACE INTO如果存在primary 或 unique相同的记录，则先删除掉，再插入新记录。

USING(相当于on条件)


--
笛卡尔积：
两表连接键不能确定唯一一条数据时，
例如A表出现3条，B表出现2条，连接结果3*2=6条


--




