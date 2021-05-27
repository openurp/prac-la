[#ftl]
[@b.head/]
[#include "nav.ftl"/]
[@urp_base.semester_bar name="semester.id" value=currentSemester/]
   <div class="search-container">
     <div class="search-panel">
    [@b.form name="laTakerSearchForm" action="!search" target="laTakerlist" title="ui.searchForm" theme="search"]
      <input type="hidden" name="laVolunteer.semester.id" value="${currentSemester.id}"/>
      [@b.select label="批次" name="laVolunteer.session.id" items=sessions empty="..."/]
      [@b.textfield name="laVolunteer.std.user.code" label="学号"/]
      [@b.textfield name="laVolunteer.std.user.name" label="姓名"/]
      [@b.select label="是否录取" name="enrolled"]
        <option value="">...</option>
        <option value="0">未录取</option>
        <option value="1">已录取</option>
      [/@]
      [@b.textfield name="laVolunteer.enrolledRank" label="录取志愿"/]
      [@b.textfield name="laVolunteer.enrolledOption.name" label="录取单位"/]
      <input type="hidden" name="orderBy" value="laVolunteer.std.user.code"/>
    [/@]
     </div>
     <div class="search-list">
    [@b.div id="laTakerlist" href="!search?laVolunteer.semester.id="+currentSemester.id+"&orderBy=laVolunteer.updatedAt desc"/]
     </div>
    </div>
[@b.foot/]
