import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { documentApi } from '../api/documentApi';

export const DOCUMENTS_QUERY_KEY = ['documents'];

export const useDocuments = () => {
  return useQuery({
    queryKey: DOCUMENTS_QUERY_KEY,
    queryFn: documentApi.fetchDocuments,
    staleTime: 1000 * 60 * 5,
  });
};

export const useUploadDocument = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (file: File) => documentApi.uploadDocument(file),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: DOCUMENTS_QUERY_KEY });
    },
  });
};

export const useDeleteDocument = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (id: string) => documentApi.deleteDocument(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: DOCUMENTS_QUERY_KEY });
    },
  });
};
