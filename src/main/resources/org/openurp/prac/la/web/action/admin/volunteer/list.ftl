[#ftl]
[@b.head/]
[@b.grid items=laVolunteers var="laVolunteer"]
  [@b.gridbar]
    bar.addItem("${b.text("action.export")}",
      action.exportData("std.code:学号,std.name:姓名,std.state.department.name:学院,std.state.major.name:专业,mobile:联系方式,enrolledRank:录取志愿,enrolledOption.corporation.name:录取单位,gpa:平均绩点,updatedAt:报名时间",null,'fileName=报名信息'));
  [/@]
  [@b.row]
    [@b.boxcol /]
      [@b.col property="std.code" title="学号" width="10%"/]
      [@b.col property="std.name" title="姓名" width="9%"]<span title="${laVolunteer.updatedAt?string('MM-dd HH:mm')}报名,${laVolunteer.mobile}">${laVolunteer.std.name}</span>[/@]
      [@b.col property="std.state.grade" title="年级" width="7%"/]
      [@b.col property="std.state.department.name" title="院系" width="16%"/]
      [@b.col property="std.state.major.name" title="专业"]${(laTaker.std.state.major.name)!}[/@]
      [@b.col property="enrolledOption.corporation.name" title="录取单位" width="16%"]
        <span style="font-size:0.8em">${laVolunteer.enrolledRank!} ${(laVolunteer.enrolledOption.corporation.name)!"--"}</span>
      [/@]
      [@b.col title="志愿信息" width="16%"]
        <span style="font-size:0.8em">
        [#list laVolunteer.takers?sort_by("rank") as t]
          ${t.rank} ${t.option.corporation.name}[#if t_has_next]<br>[/#if]
        [/#list]
        </span>
      [/@]
      [@b.col property="gpa"  title="平均绩点" width="6%"/]
      [@b.col property="attachmentPath"  title="附件" width="6%"]
        [#if laVolunteer.attachmentPath??][@b.a href="!download?laVolunteer.id="+laVolunteer.id target="_blank"]<i class="fa-solid fa-paperclip"></i>[/@][/#if]
      [/@]
  [/@]
[/@]

[@b.foot/]
