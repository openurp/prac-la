[#ftl]
[@b.head/]
[#include "../volunteer/nav.ftl"/]
[@urp_base.semester_bar name="semester.id" value=currentSemester/]
   <div class="search-container">
     <div class="search-panel">
    [@b.form name="laTakerSearchForm" action="!search" target="laTakerlist" title="ui.searchForm" theme="search"]
      <input type="hidden" name="laTaker.option.semester.id" value="${currentSemester.id}"/>
      [@b.select label="批次" name="laTaker.option.session.id" items=sessions empty="..."/]
      [@b.textfield name="laTaker.volunteer.std.user.code" label="学号"/]
      [@b.textfield name="laTaker.volunteer.std.user.name" label="姓名"/]
      [@b.select label="是否录取" name="laTaker.enrolled"]
        <option value="">...</option>
        <option value="0">未录取</option>
        <option value="1">已录取</option>
      [/@]
      [@b.textfield name="laTaker.rank" label="报名志愿"/]
      [@b.textfield name="laTaker.option.corporation.name" label="报名单位"/]
      <input type="hidden" name="orderBy" value="laTaker.volunteer.std.user.code"/>
    [/@]
     </div>
     <div class="search-list">
    [@b.div id="laTakerlist" href="!search?laTaker.option.semester.id="+currentSemester.id+"&orderBy=laTaker.updatedAt desc"/]
     </div>
    </div>
[@b.foot/]
