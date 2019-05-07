[#ftl]
[@b.head/]
[@b.toolbar title="法律助理报名"]bar.addBack();[/@]
[@b.messages/]

[#if sessions??&&sessions?size==1]
[#assign session=sessions?first]
<div class="container">
<div class="jumbotron">
  <h2>法律助理报名开始了<span style="font-size:0.5em">(${session.beginAt?string("MM-dd HH:mm")}~${session.endAt?string("MM-dd HH:mm")})</span></h2>
  <p>
 为充分利用行业优质教育资源，为学生创造并提供丰富的专业实践锻炼机会，提高学生的法学实践与创新能力，现启动上海市高级人民法院与我校联合举办的本科生法律助理项目的${session.semester.beginOn?string('YYYY')}报名工作 。
  </p>
  <p>
  [#if session.noticeUrl??]
  <a href="${session.noticeUrl}" class="btn btn-primary btn-lg" target="_new">查看完整通知</a>
  [/#if]
  [#if volunteers?size>0]
  [#list volunteers as v]
  [@b.a class="btn btn-primary btn-lg" href="!signup?volunteer.id="+v.id+"&session.id="+session.id role="button"]进入我的报名[/@]
  [/#list]
  [#else]
  [@b.a class="btn btn-primary btn-lg" href="!signup?session.id="+session.id role="button"]我要报名[/@]
  [/#if]
  </p>
</div>
</div>
[#else]
   不在立项时间。
[/#if]
[@b.foot/]
