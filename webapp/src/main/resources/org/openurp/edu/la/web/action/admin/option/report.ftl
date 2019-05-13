[#ftl]
[@b.head/]
[@b.toolbar title="单位面试表"/]
[#list options as option]
<h3 style="text-align:center">华东政法大学&nbsp;&nbsp;${option.semester.schoolYear}学年<br>${option.corporation.name}&nbsp;&nbsp;法律助理面试名单</h3>
<table class="gridtable">
 <thead class="gridhead">
  <tr>
   <th rowspan="2" width="5%" style="border-width: 0 1px 1px 1px;">序号</th>
   <th rowspan="2" width="10%">姓名</th>
   <th rowspan="2" width="16%">学院</th>
   <th rowspan="2" width="10%">专业</th>
   <th rowspan="2" width="15%">学号</th>
   <th rowspan="2" width="15%">联系电话</th>
   <th rowspan="2" width="5%">绩点</th>
   <th colspan="2" width="14%">志愿填报情况 </th>
   <th rowspan="2" width="10%">建议入选请打“√”</th>
  </tr>
  <tr>
     <th width="7%">一志愿</th>
     <th width="7%">二志愿</th>
  </tr>
 </thead>
 [#list option.volunteers?sort_by(["std","user","code"]) as volunteer]
  <tr>
    <td style="border-width: 0 1px 1px 1px;">${volunteer_index+1}</td>
    <td>${volunteer.std.user.name}</td>
    <td><span style="font-size:0.8em">${volunteer.std.state.department.name}</span></td>
    <td>${volunteer.std.state.major.name}</td>
    <td>${volunteer.std.user.code}</td>
    <td>${volunteer.mobile}</td>
    <td>${volunteer.gpa?string(".00")}</td>
    <td>[#if (volunteer.enrolledRank!0)==1]<span class="glyphicon glyphicon-ok"></span>[/#if]</td>
    <td>[#if (volunteer.enrolledRank!0)==2]<span class="glyphicon glyphicon-ok"></span>[/#if]</td>
    <td></td>
  </tr>
  [/#list]
</table>
[#if option_has_next]<div style="PAGE-BREAK-AFTER: always"></div>[/#if]
[/#list]
[@b.foot/]
