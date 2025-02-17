package com.sendback.domain.project.controller;

import com.sendback.domain.project.dto.request.SaveProjectRequestDto;
import com.sendback.domain.project.dto.request.UpdateProjectRequestDto;
import com.sendback.domain.project.dto.response.GetProjectsResponseDto;
import com.sendback.domain.project.dto.response.ProjectDetailResponseDto;
import com.sendback.domain.project.dto.response.ProjectIdResponseDto;
import com.sendback.domain.project.dto.response.RecommendedProjectResponseDto;
import com.sendback.domain.project.dto.response.PullUpProjectResponseDto;
import com.sendback.domain.project.service.ProjectService;
import com.sendback.global.common.ApiResponse;
import com.sendback.global.common.CustomPage;
import com.sendback.global.common.UserId;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import static com.sendback.global.common.ApiResponse.success;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping
    public ApiResponse<CustomPage<GetProjectsResponseDto>> getProjects(
            @PageableDefault(page = 1, size = 5) Pageable pageable,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String field,
            @RequestParam(required = false) Boolean isFinished,
            @RequestParam(required = false) Long sort
            ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getPrincipal() == "anonymousUser") {
            return success(projectService.getProjects(null , pageable, keyword, field, isFinished, sort));
        }

        Long userId = (Long) authentication.getPrincipal();
        return success(projectService.getProjects(userId, pageable, keyword, field, isFinished, sort));
    }

    @GetMapping("/{projectId}")
    public ApiResponse<ProjectDetailResponseDto> getProjectDetail(
            @PathVariable Long projectId
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getPrincipal() == "anonymousUser") {
            return success(projectService.getProjectDetail(null , projectId));
        }

        Long userId = (Long) authentication.getPrincipal();
        return success(projectService.getProjectDetail(userId, projectId));
    }

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ApiResponse<ProjectIdResponseDto> saveProject(
            @UserId Long userId,
            @RequestPart(value = "data") @Valid SaveProjectRequestDto saveProjectRequestDto,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {

        return success(projectService.saveProject(userId, saveProjectRequestDto, images));
    }

    @PutMapping(value = "/{projectId}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ApiResponse<ProjectIdResponseDto> updateProject(
            @UserId Long userId,
            @PathVariable Long projectId,
            @RequestPart(value = "data") @Valid UpdateProjectRequestDto updateProjectRequestDto,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {

        return success(projectService.updateProject(userId, projectId, updateProjectRequestDto, images));
    }

    @DeleteMapping("/{projectId}")
    public ApiResponse<Object> deleteProject(
            @UserId Long userId,
            @PathVariable Long projectId) {

        projectService.deleteProject(userId, projectId);

        return success(null);
    }


    @GetMapping("/recommend")
    public ApiResponse<List<RecommendedProjectResponseDto>> getRecommendedProject() {
        return success(projectService.getRecommendedProject());
    }

    @PutMapping("/{projectId}/pull-up")
    public ApiResponse<PullUpProjectResponseDto> pullUpProject(
            @UserId Long userId,
            @PathVariable Long projectId) {

        return success(projectService.pullUpProject(userId, projectId));
    }
}
