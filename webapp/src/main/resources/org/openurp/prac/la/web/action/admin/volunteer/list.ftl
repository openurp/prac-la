[#ftl]
[@b.head/]
[@b.grid items=laTakers var="laTaker"]
  [@b.gridbar]
    bar.addItem("手工录取",action.edit());
    bar.addItem("${b.text("action.delete")}",action.remove());
    bar.addItem("${b.text("action.export")}",
      action.exportData("volunteer.std.user.code:学号,volunteer.std.user.name:姓名,volunteer.std.state.department.name:学院,volunteer.std.state.major.name:专业,volunteer.mobile:联系方式,rank:志愿顺序,option.corporation.name:单位,volunteer.gpa:平均绩点,updatedAt:报名时间",null,'fileName=志愿报名信息'));
  [/@]
  [@b.row]
    [@b.boxcol /]
      [@b.col property="rank" title="志愿" width="5%"/]
      [@b.col property="volunteer.std.user.code" title="学号" width="10%"/]
      [@b.col property="volunteer.std.user.name" title="姓名" width="9%"]<span title="${laTaker.updatedAt?string('MM-dd HH:mm')}报名,${laTaker.volunteer.mobile}">${laTaker.volunteer.std.user.name}</span>[/@]
      [@b.col property="volunteer.std.state.grade" title="年级" width="8%"/]
      [@b.col property="volunteer.std.state.department.name" title="院系" width="17%"/]
      [@b.col property="volunteer.std.state.major.name" title="专业" width="15%"]${(laTaker.volunteer.std.state.major.name)!}[/@]
      [@b.col property="option.corporation.name" title="单位" width="20%"]
      [#if laTaker.option.id=(laTaker.volunteer.enrolledOption.id)!0]<i class="fa fa-check" aria-hidden="true"></i>[/#if]${laTaker.option.corporation.name}
      [/@]
      [@b.col property="volunteer.gpa"  title="平均绩点" width="6%"/]
      [@b.col property="updatedAt" title="报名时间" width="10%"]
        ${laTaker.volunteer.updatedAt?string('MM-dd HH:mm')}
      [/@]
  [/@]
[/@]

[@b.foot/]
